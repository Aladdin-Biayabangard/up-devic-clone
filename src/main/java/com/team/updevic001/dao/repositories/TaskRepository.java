package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId")
    List<Task> findTaskByCourseId(@Param("courseId") String courseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.course.id = :id")
    void deleteTaskByCourseId(String id);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.course.id = :courseId")
    long countByCourseId(@Param("courseId") String courseId);

    @Query("SELECT t.id FROM Task t WHERE t.course.id = :courseId")
    List<Long> findIdsByCourseId(@Param("courseId") String courseId);
}
