package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.CourseMapper;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.StudentCourse;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.CourseRepository;
import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.services.interfaces.StudentService;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void enrollInCourse(Long courseId, User student) {
        Course course = courseServiceImpl.findCourseById(courseId);
        if (isAlreadyEnrolledInCourse(student, course)) {
            throw new IllegalStateException("Student is already enrolled in this course!");
        }
        enrollStudentInCourse(student, course);
    }

    @Override
    @Transactional
    public void unenrollUserFromCourse(Long courseId) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);
        if (!isAlreadyEnrolledInCourse(student, course)) {
            throw new IllegalStateException("Only students can unenroll from courses!");
        }
        studentCourseRepository.deleteStudentCourseByCourseAndStudent(course, student);
    }

    @Override
    public ResponseCourseShortInfoDto getStudentCourse(Long courseId) {
        User student = authHelper.getAuthenticatedUser();
        Course course = courseRepository
                .findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Not found course !"));
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

//    @Override
//    public List<ResponseFullCourseDto> getStudentLessons() {
//        User student = authHelper.getAuthenticatedUser();
//        log.info("Fetching lessons for student with ID: {}", student.getId());
//        List<Course> courses = studentCourseRepository.findCourseByStudent(student);
//        return courseMapper.toFullResponse(courses);
//    }


//    @Override
//    public void deleteStudentLessonComment(String userId, String lessonId, String commentId) {
//        log.info("Attempting to delete comment with ID: {} from lesson with ID: {} for student with ID: {}", commentId, lessonId, userId);
//
//        User user = adminServiceImpl.findUserById(userId);
//        Lesson lesson = lessonServiceImpl.findLessonById(lessonId);
//        Comment comment = commentServiceImpl.findCommentById(commentId);
//
//        Comment findComment = user.getComments().stream()
//                .filter(comm -> comm.getLesson().equals(lesson) && comm.getId().equals(comment.getId()))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("User has no comment for this lesson"));
//
//        user.getComments().remove(findComment);
//        userRepository.save(user);
//        commentRepository.delete(comment);
//
//        log.info("Successfully deleted comment with ID: {} from lesson with ID: {}", commentId, lessonId);
    /*

    @Override
     public void deleteStudentCourseComment(String userId, String courseId, String commentId) {
         log.info("Attempting to delete comment with ID: {} from course with ID: {} for student with ID: {}", commentId, courseId, userId);

         User user = adminServiceImpl.findUserById(userId);
         Course course = courseServiceImpl.findCourseById(courseId);

         Comment comment = commentServiceImpl.findCommentById(commentId);
         Comment findComment = user.getComments().stream()
                 .filter(comm -> comm.getCourse().equals(course) && comm.getId().equals(comment.getId()))
                 .findFirst()
                 .orElseThrow(() -> new IllegalArgumentException("User has no comment for this course"));

         user.getComments().remove(findComment);
         userRepository.save(user);
         commentRepository.delete(comment);

         log.info("Successfully deleted comment with ID: {} from course with ID: {}", commentId, courseId);
    // Helper methods


    private Course findCourseById(String courseId) {
        log.debug("Looking for course with ID: {}", courseId);
        return courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
    }

    private Student castToStudent(User user) {
        if (!(user instanceof Student)) {
            log.error("User with ID: {} is not a student!", user.getUuid());
            throw new IllegalStateException("User is not a student!");
        }
        return (Student) user;
    }

    private boolean isAlreadyEnrolledInCourse(Student student, Course course) {
        return studentCourseRepository.existsByCourseAndStudent(course, student);
    }

    private void enrollStudentInCourse(Student student, Course course) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourse(course);
        studentCourse.setStudent(student);
        studentCourse.setStatus(Status.ENROLLED);
        studentCourseRepository.save(studentCourse);
    }
*/
