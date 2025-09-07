package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.config.ai.AiGradeResult;
import com.team.updevic001.configuration.config.ai.AiGradingService;
import com.team.updevic001.configuration.config.ai.TaskResultDto;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.StudentTask;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.CourseRepository;
import com.team.updevic001.dao.repositories.LessonRepository;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserLessonStatusRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.request.AnswerDto;
import com.team.updevic001.model.dtos.request.TaskDto;
import com.team.updevic001.model.dtos.response.task.ResponseSubmission;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;
import com.team.updevic001.services.interfaces.TaskService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.team.updevic001.model.enums.ExceptionConstants.COURSE_NOT_FOUND;
import static com.team.updevic001.model.enums.ExceptionConstants.TASK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TestResultRepository testResultRepository;
    private final CourseRepository courseRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final CourseServiceImpl courseServiceImpl;
    private final AuthHelper authHelper;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final LessonRepository lessonRepository;
    private final AiGradingService aiGradingService;

    @Override
    @Transactional
    public void createTask(String courseId, TaskDto taskDto) {
        Course course = courseRepository.findCourseById(courseId)
                .orElseThrow(() -> new NotFoundException(COURSE_NOT_FOUND.getCode(),
                        COURSE_NOT_FOUND.getMessage().formatted(courseId)));

        User teacher = authHelper.getAuthenticatedUser();
        courseServiceImpl.validateAccess(courseId, teacher);

        Task task = new Task();
        task.setQuestions(taskDto.getQuestions());
        if (taskDto.getOptions() != null) {
            task.setOptions(taskDto.getOptions());
        }
        task.setCorrectAnswer(taskDto.getCorrectAnswer());
        task.setCourse(course);

        if (course.getTasks() == null) {
            course.setTasks(new ArrayList<>());
        }
        course.getTasks().add(task);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public TaskResultDto checkAnswer(String courseId, Long taskId, AnswerDto answerDto) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);

        if (!userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(course, student)) {
            throw new IllegalArgumentException("You are not enrolled in this course.");
        }

        if (!areAllLessonsWatched(student, course)) {
            throw new IllegalArgumentException("You must watch all lessons before taking the test.");
        }

        Task task = findTaskById(taskId);
        ensureTaskNotCompleted(student, task);

        AiGradeResult aiResult = aiGradingService.check(
                task.getQuestions(),
                task.getCorrectAnswer(),
                answerDto.getAnswer()
        );

        double taskScore = aiResult.getScore(); // <-- AI-dan gələn bal

        TestResult result = testResultRepository
                .findTestResultByStudentAndCourse(student, course)
                .orElseGet(() -> {
                    TestResult newResult = new TestResult();
                    newResult.setScore(0);
                    newResult.setCourse(course);
                    newResult.setStudent(student);
                    return newResult;
                });

        result.setScore(result.getScore() + taskScore);
        testResultRepository.save(result);

        // Task tamamlandı kimi qeyd et
        StudentTask studentTask = new StudentTask();
        studentTask.setCompleted(true);
        studentTask.setStudent(student);
        studentTask.setStudentAnswer(answerDto.getAnswer());
        studentTask.setFeedback(aiResult.getFeedback());
        studentTask.setTask(task);
        studentTask.setCorrectAnswer(task.getCorrectAnswer());
        studentTask.setScore(taskScore);
        studentTask.setSubmitted(true);
        studentTask.setCorrect(aiResult.getCorrect());
        studentTaskRepository.save(studentTask);

        return new TaskResultDto(
                aiResult.getCorrect(),
                taskScore,
                aiResult.getFeedback(),
                aiResult.getCorrectAnswer(),
                answerDto.getAnswer(),
                true
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<ResponseTaskDto> getTasks(String courseId) {
        User student = authHelper.getAuthenticatedUser();

        return taskRepository.findTaskByCourseId(courseId).stream()
                .map(task -> {
                    var submitted = false;

                    var studentTaskOpt = studentTaskRepository.existsByStudentAndTask(student, task);
                    if (studentTaskOpt) {
                        submitted = true;
                    }

                    return new ResponseTaskDto(
                            task.getId(),
                            task.getQuestions(),
                            task.getOptions(),
                            task.getCorrectAnswer(),
                            submitted
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSubmission> getSubmissionTasks(String courseId) {
        User student = authHelper.getAuthenticatedUser();

        List<Task> tasks = taskRepository.findTaskByCourseId(courseId);

        List<StudentTask> studentTasks = studentTaskRepository.findByStudentAndTaskIn(student, tasks);

        // Map: taskId -> studentTask
        Map<Long, StudentTask> studentTaskMap = studentTasks.stream()
                .collect(Collectors.toMap(st -> st.getTask().getId(), st -> st));

        return tasks.stream()
                .map(task -> {
                    StudentTask st = studentTaskMap.get(task.getId());
                    if (st != null) {
                        return new ResponseSubmission(
                                st.getScore(),
                                st.getFeedback(),
                                st.getCorrectAnswer(),
                                st.getStudentAnswer(),
                                st.isCorrect(),
                                st.isSubmitted()
                        );
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }


    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private boolean areAllLessonsWatched(User user, Course course) {
        List<String> lessonIds = lessonRepository.findLessonIdsByCourseId(course.getId());
        return lessonIds.stream()
                .allMatch(lessonId -> userLessonStatusRepository.existsWatchedByUserAndLesson(user, lessonId));
    }

    private void ensureTaskNotCompleted(User student, Task task) {
        if (studentTaskRepository.existsStudentTaskByCompletedAndStudentAndTask(true, student, task)) {
            throw new IllegalArgumentException("You have already completed this task.");
        }
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(TASK_NOT_FOUND.getCode(),
                        TASK_NOT_FOUND.getMessage().formatted(taskId)));
    }

}
