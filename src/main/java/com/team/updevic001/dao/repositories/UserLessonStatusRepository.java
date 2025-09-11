package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.UserLessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserLessonStatusRepository extends JpaRepository<UserLessonStatus, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM UserLessonStatus ul WHERE ul.lesson.id IN :ids")
    void deleteUserLessonStatusByLessonsId(List<String> ids);

    @Query("""
        SELECT COUNT(uls)
        FROM UserLessonStatus uls
        JOIN uls.lesson l
        WHERE uls.user.id = :userId
          AND l.course.id = :courseId
          AND uls.isWatched = true
    """)
    long countWatchedByUserAndCourse(@Param("userId") Long userId, @Param("courseId") String courseId);
}
