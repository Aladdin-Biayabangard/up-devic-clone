package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserLessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserLessonStatusRepository extends JpaRepository<UserLessonStatus, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM UserLessonStatus ul WHERE ul.lesson.id IN :ids")
    void deleteUserLessonStatusByLessonsId(List<String> ids);

    @Query("SELECT CASE WHEN COUNT(uls) > 0 THEN TRUE ELSE FALSE END " +
           "FROM UserLessonStatus uls " +
           "WHERE uls.user = :user AND uls.lesson.id = :lessonId AND uls.isWatched = TRUE")
    boolean existsWatchedByUserAndLesson(User user, String lessonId);
}
