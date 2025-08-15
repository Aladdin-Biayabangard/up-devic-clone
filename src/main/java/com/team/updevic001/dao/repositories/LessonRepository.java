package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.dao.entities.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findLessonByCourseId(Long courseId);

    @Query("SELECT l.id FROM Lesson l WHERE l.course.id=:courseId")
    List<Long> findLessonIdsByCourseId(Long courseId);

    List<Lesson> findLessonsByTeacherId(Long id);

    @Query("SELECT l.photoKey FROM Lesson l WHERE l.id=:id")
    Optional<String> findLessonPhotoKeyByLessonId(Long id);

    @Query("SELECT l.videoKey FROM Lesson l WHERE l.id=:id")
    Optional<String> findLessonVideoKeyByLessonId(Long id);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
           "FROM Lesson l WHERE l.teacher = :teacher AND l = :lesson")
    boolean existsLessonByTeacherAndLesson(@Param("teacher") Teacher teacher,
                                           @Param("lesson") Lesson lesson);

    @Transactional
    @Modifying
    @Query("UPDATE Lesson l SET l.photoUrl=:fileUrl,l.photoKey=:fileKey WHERE l.id=:id ")
    void updateCourseFileInfo(Long id, String fileKey, String fileUrl);

    @Query("SELECt l.id FROM Lesson l WHERE l.course.id=:id")
    List<Long> findAllLessonIdsByCourseId(Long id);


}
