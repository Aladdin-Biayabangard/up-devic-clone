package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.exceptions.ResourceNotFoundException;
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
import java.util.stream.Collectors;

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
    private final TeacherServiceImpl teacherServiceImpl;
    private final AuthHelper authHelper;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public void createTask(Long courseId, TaskDto taskDto) {
        Course course = courseRepository.findCourseById(courseId).orElseThrow(()-> new ResourceNotFoundException("COURSE_NOT_FOUND"));
        Teacher teacher = teacherServiceImpl.getAuthenticatedTeacher();
        courseServiceImpl.validateAccess(courseId, teacher);
        Task task = modelMapper.map(taskDto, Task.class);
        List<String> options = formatedOptions(taskDto.getOptions());
        task.setOptions(options);
        task.setCorrectAnswer(taskDto.getCorrectAnswer());
        course.getTasks().add(task);
        task.setCourse(course);
        taskRepository.save(task);
    }

    @Override
    public void checkAnswer(Long courseId, Long taskId, AnswerDto answerDto) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);
        boolean enrolled = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(course, student);
        if (!enrolled) {
            throw new IllegalArgumentException("This student is not enrolled in this course.");
        }
        boolean allVideosWatched = areAllLessonsWatched(student, course);
        if (!allVideosWatched) {
            throw new IllegalArgumentException("You need to watch the lessons to watch the tests.");
        }
        Task task = findTaskById(taskId);
        checkCompletionTask(student, task);
        TestResult result = checkTestResult(student, course);
        validateAnswerAndUpdateScore(student, result, task, answerDto, course);
    }

    @Override
    public List<ResponseTaskDto> getTasks(Long courseId) {
        List<Task> tasks = taskRepository.findTaskByCourseId(courseId);
        return tasks.stream().map(task -> modelMapper.map(task, ResponseTaskDto.class)).toList();
    }

    private boolean areAllLessonsWatched(User user, Course course) {
        List<Long> lessonIds = lessonRepository.findLessonIdsByCourseId(course.getId());
        for (Long lessonId : lessonIds) {
            if (!userLessonStatusRepository.findByUserAndLesson(user, lessonId)) {
                return false;
            }
        }
        return true;
    }

    private void checkCompletionTask(User student, Task task) {
        if (studentTaskRepository.existsStudentTaskByCompletedAndStudentAndTask(true, student, task)) {
            throw new IllegalArgumentException("This question has already been answered.");
        }
    }

    private TestResult checkTestResult(User student, Course course) {
        return testResultRepository
                .findTestResultByStudentAndCourse(student, course).orElseGet(
                        () -> {
                            TestResult testResult = new TestResult();
                            testResult.setScore(0);
                            testResult.setCourse(course);
                            testResult.setStudent(student);
                            return testResult;
                        });
    }

    private void validateAnswerAndUpdateScore(User student, TestResult result, Task task, AnswerDto answerDto, Course course) {
        String correctAnswer = task.getCorrectAnswer();

        double percent = calculateScore(course);
        StudentTask studentTask = new StudentTask();

        if (correctAnswer.contains(answerDto.getAnswer())) {
            result.setScore(result.getScore() + percent);
            testResultRepository.save(result);
            studentTask.setCompleted(true);
        } else {
            studentTask.setCompleted(false);
            throw new IllegalArgumentException("Incorrect answer!");
        }
        studentTask.setStudent(student);
        studentTask.setTask(task);
        studentTaskRepository.save(studentTask);
    }

    private List<String> formatedOptions(List<String> options) {
        final char[] optionChar = {'A'};
        return options.stream()
                .map(option -> optionChar[0]++ + ") " + option)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private double calculateScore(Course course) {
        int taskCount = course.getTasks().size();
        return (double) 100 / taskCount;
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TASK_NOT_FOUND"));
    }
}

