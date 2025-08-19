package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.CommentMapper;
import com.team.updevic001.dao.entities.Comment;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.CommentRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.PaymentStatusException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;
import com.team.updevic001.services.interfaces.CommentService;
import com.team.updevic001.services.interfaces.TeacherService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final AuthHelper authHelper;
    private final CourseServiceImpl courseServiceImpl;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final LessonServiceImpl lessonServiceImpl;
    private final TeacherService teacherService;

    @Override
    @Transactional
    @CacheEvict(value = "courseComments", key = "{#courseId, 0}")
    public ResponseCommentDto addCommentToCourse(String courseId, CommentDto commentDto) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);

        boolean isHeadTeacher = course.getHeadTeacher().equals(teacherService.getAuthenticatedTeacher());
        boolean hasPaid = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(course, authenticatedUser);

        if (!isHeadTeacher && !hasPaid) {
            throw new PaymentStatusException("Access denied. You must be the course owner or a paying student to comment.");
        }

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .user(authenticatedUser)
                .course(course)
                .build();

        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Override
    @CacheEvict(value = "lessonComments", key = "{#lessonId, 0}")
    public ResponseCommentDto addCommentToLesson(String lessonId, CommentDto commentDto) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Lesson lesson = lessonServiceImpl.findLessonById(lessonId);
        boolean isTeacher = lesson.getTeacher().equals(teacherService.getAuthenticatedTeacher());
        boolean hasPaid = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(lesson.getCourse(), authenticatedUser);
        if (!hasPaid && !isTeacher) {
            throw new PaymentStatusException("Access denied. You must be the course owner or a paying student to comment.");

        }
        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .user(authenticatedUser)
                .lesson(lesson)
                .build();
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }


    public void updateComment(Long commentId, CommentDto commentDto) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().equals(authenticatedUser)) {
            throw new ForbiddenException("NOT_ALLOWED_UPDATE_COMMENT");
        }
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
    }

    @Override
    @Cacheable(value = "courseComments", key = "{#courseId, #request.page, #request.size}")
    public CustomPage<ResponseCommentDto> getCourseComment(String courseId, CustomPageRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<Comment> comments = commentRepository.findCommentByCourseId(courseId, pageable);
        return new CustomPage<>(
                commentMapper.toDto(comments.getContent()),
                comments.getNumber(),
                comments.getSize());
    }

    @Override
    @Cacheable(value = "lessonComments", key = "{#lessonId, #request.page, #request.size}")
    public CustomPage<ResponseCommentDto> getLessonComment(String lessonId, CustomPageRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<Comment> lessons = commentRepository.findCommentByLessonId(lessonId, pageable);
        return new CustomPage<>(
                commentMapper.toDto(lessons.getContent()),
                lessons.getNumber(),
                lessons.getSize());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courseComments", key = "{#result.course?.id, 0}", condition = "#result.course != null"),
            @CacheEvict(value = "lessonComments", key = "{#result.lesson?.id, 0}", condition = "#result.lesson != null")
    })
    public void deleteComment(Long commentId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Comment comment = findCommentById(commentId);
        if (comment.getUser().equals(authenticatedUser)) {
            commentRepository.deleteById(commentId);
        }
        throw new ForbiddenException("NOT_ALLOWED_DELETE_COMMENT");

    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with ID " + commentId + " not found"));
    }
}
