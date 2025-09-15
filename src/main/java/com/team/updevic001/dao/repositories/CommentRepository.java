package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.course.Comment;
import com.team.updevic001.dao.entities.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findCommentByCourseId(String id, Pageable pageable);

    Page<Comment> findCommentByLessonId(String id, Pageable pageable);

    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :id AND c.course.teacher = :user")
    boolean checkCourseTeacher(Long id, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.lesson.id IN :ids")
    void deleteCommentsByLessonsId(List<String> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.course.id=:id")
    void deleteCommentsByCourseId(String id);
}
