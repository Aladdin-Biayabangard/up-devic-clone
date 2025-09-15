package com.team.updevic001.services.impl.student;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.mappers.CourseMapper;
import com.team.updevic001.dao.entities.course.Course;
import com.team.updevic001.dao.entities.StudentCourse;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.repositories.CourseRepository;
import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.services.impl.course.CourseServiceImpl;
import com.team.updevic001.services.interfaces.StudentService;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.team.updevic001.exceptions.ExceptionConstants.COURSE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentCourseRepository studentCourseRepository;
    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CourseServiceImpl courseServiceImpl;
    private final AuthHelper authHelper;

    @Override
    @Transactional
    public void enrollInCourse(String courseId, User student) {
        Course course = courseServiceImpl.findCourseById(courseId);
        if (isAlreadyEnrolledInCourse(student, course)) {
            throw new IllegalArgumentException("Student is already enrolled in this course!");
        }
        enrollStudentInCourse(student, course);
    }

    @Override
    @Transactional
    public void unenrollUserFromCourse(String courseId) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);
        if (!isAlreadyEnrolledInCourse(student, course)) {
            throw new IllegalStateException("Only students can unenroll from courses!");
        }
        studentCourseRepository.deleteStudentCourseByCourseAndStudent(course, student);
    }

    @Override
    public ResponseCourseShortInfoDto getStudentCourse(String courseId) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseRepository
                .findById(courseId).orElseThrow(() -> new NotFoundException(COURSE_NOT_FOUND.getCode(),
                        COURSE_NOT_FOUND.getMessage().formatted(courseId)));
        if (!studentCourseRepository
                .existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("This student does not have such a course");
        }
        return courseMapper.toCourseResponse(course);
    }

    @Override
    public List<ResponseCourseShortInfoDto> getStudentCourses() {
        User student = authHelper.getAuthenticatedUser();
        List<Course> courseByStudent = studentCourseRepository.findCourseByStudent(student);
        return courseByStudent.stream()
                .map(courseMapper::toCourseResponse).toList();
    }

    private boolean isAlreadyEnrolledInCourse(User student, Course course) {
        return studentCourseRepository.existsByCourseAndStudent(course, student);
    }

    private void enrollStudentInCourse(User student, Course course) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourse(course);
        studentCourse.setStudent(student);
        studentCourse.setStatus(Status.ENROLLED);
        studentCourseRepository.save(studentCourse);
    }
}
