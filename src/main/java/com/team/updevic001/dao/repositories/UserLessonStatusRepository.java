package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserLessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserLessonStatusRepository extends JpaRepository<UserLessonStatus, Long> {

    @Query("SELECT uls.isWatched FROM UserLessonStatus uls WHERE uls.user=:user and uls.lesson.id=:lessonId")
    boolean findByUserAndLesson(User user, String lessonId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserLessonStatus ul WHERE ul.lesson.id IN :ids")
    void deleteUserLessonStatusByLessonsId(List<String> ids);


    boolean existsByUserAndLessonId(User user, String lessonId);
}
