package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.request.AnswerDto;
import com.team.updevic001.model.dtos.request.TaskDto;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;
import com.team.updevic001.services.interfaces.TaskService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static com.team.updevic001.model.enums.ExceptionConstants.COURSE_NOT_FOUND;
import static com.team.updevic001.model.enums.ExceptionConstants.TASK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final TestResultRepository testResultRepository;
    private final CourseRepository courseRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final CourseServiceImpl courseServiceImpl;
    private final AuthHelper authHelper;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final LessonRepository lessonRepository;

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
        task.setOptions(formatOptions(taskDto.getOptions()));
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
    public void checkAnswer(String courseId, Long taskId, AnswerDto answerDto) {
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

        TestResult result = testResultRepository
                .findTestResultByStudentAndCourse(student, course)
                .orElseGet(() -> {
                    TestResult newResult = new TestResult();
                    newResult.setScore(0);
                    newResult.setCourse(course);
                    newResult.setStudent(student);
                    return newResult;
                });

        if (task.getCorrectAnswer().equalsIgnoreCase(answerDto.getAnswer())) {
            double scorePerTask = calculateScore(course.getId());
            result.setScore(result.getScore() + scorePerTask);
            testResultRepository.save(result);

            StudentTask studentTask = new StudentTask();
            studentTask.setCompleted(true);
            studentTask.setStudent(student);
            studentTask.setTask(task);
            studentTaskRepository.save(studentTask);
        } else {
            throw new IllegalArgumentException("Incorrect answer!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseTaskDto> getTasks(String courseId) {
        return taskRepository.findTaskByCourseId(courseId).stream()
                .map(task -> new ResponseTaskDto(task.getQuestions(), task.getOptions()))
                .toList();
    }

    private boolean areAllLessonsWatched(User user, Course course) {
        List<String> lessonIds = lessonRepository.findLessonIdsByCourseId(course.getId());
        return lessonIds.stream()
                .allMatch(lessonId -> userLessonStatusRepository.findByUserAndLesson(user, lessonId));
    }

    private void ensureTaskNotCompleted(User student, Task task) {
        if (studentTaskRepository.existsStudentTaskByCompletedAndStudentAndTask(true, student, task)) {
            throw new IllegalArgumentException("You have already completed this task.");
        }
    }

    private double calculateScore(String courseId) {
        int taskCount = taskRepository.countByCourseId(courseId);
        return taskCount > 0 ? (100.0 / taskCount) : 0;
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(TASK_NOT_FOUND.getCode(),
                        TASK_NOT_FOUND.getMessage().formatted(taskId)));
    }

    private List<String> formatOptions(List<String> options) {
        final char[] optionChar = {'A'};
        return options.stream()
                .map(option -> optionChar[0]++ + ") " + option)
                .toList();
    }
}
