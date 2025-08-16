package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findTaskByCourseId(String courseId);


    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.course.id = :id")
    void deleteTaskByCourseId(String id);
}
