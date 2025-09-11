package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.dao.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    List<Lesson> findByCourseIdOrderByCreatedAtAsc(String courseId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
           "FROM Lesson l WHERE l.teacher = :teacher AND l = :lesson")
    boolean existsLessonByTeacherAndLesson(@Param("teacher") User teacher,
                                           @Param("lesson") Lesson lesson);

    @Transactional
    @Modifying
    @Query("UPDATE Lesson l SET l.photoUrl=:fileUrl,l.photoKey=:fileKey WHERE l.id=:id ")
    void updateCourseFileInfo(String id, String fileKey, String fileUrl);

    @Query("SELECt l.id FROM Lesson l WHERE l.course.id=:id")
    List<String> findAllLessonIdsByCourseId(String id);


    long countByCourseId(String id);

}
