package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteService {

    private final CourseRepository courseRepository;

    private final TeacherCourseRepository teacherCourseRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final WishListRepository wishListRepository;
    private final LessonRepository lessonRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final TestResultRepository testResultRepository;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final CertificateRepository certificateRepository;
    private final FileLoadServiceImpl fileLoadService;

    public void deleteCourseAndReferencedData(String courseId, List<String> lessonIds, User user) {
        wishListRepository.deleteWishListByCourseIdAndUser(courseId, user);
        userLessonStatusRepository.deleteUserLessonStatusByLessonsId(lessonIds);
        commentRepository.deleteCommentsByLessonsId(lessonIds);
        commentRepository.deleteCommentsByCourseId(courseId);
        lessonRepository.deleteAllById(lessonIds);
        certificateRepository.deleteCertificateByCourseId(courseId);
        courseRatingRepository.deleteRatingByCourseId(courseId);
        studentCourseRepository.deleteStudentCourseByCourseId(courseId);
        taskRepository.deleteTaskByCourseId(courseId);
        teacherCourseRepository.deleteTeacherCourseByCourseId(courseId);
        userCourseFeeRepository.deleteCourseFeeByCourseId(courseId);
        testResultRepository.deleteAllByCourseId(courseId);
        courseRatingRepository.deleteAllByCourseId(courseId);
        courseRepository.deleteById(courseId);
    }

    public void deleteLessonAndReferencedData(Lesson lesson, String lessonId) {
        fileLoadService.deleteFileFromAws(lesson.getVideoKey());
        fileLoadService.deleteFileFromAws(lesson.getPhotoKey());
        userLessonStatusRepository.deleteUserLessonStatusByLessonsId(List.of(lessonId));
        commentRepository.deleteCommentsByLessonsId(List.of(lessonId));
        lessonRepository.delete(lesson);
    }
}
