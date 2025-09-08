package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.StudentTask;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    long countAllByTaskIdIn(List<Long> taskIds);

    boolean existsStudentTaskByCompletedAndStudentAndTask(Boolean completed, User student, Task task);

    @Query("SELECT st FROM StudentTask st WHERE st.student = :student AND st.task IN :tasks")
    List<StudentTask> findByStudentAndTaskIn(@Param("student") User student,
                                             @Param("tasks") List<Task> tasks);

    Set<Long> findSubmittedTaskIdsByStudentAndTaskIn(User student, List<Task> tasks);

    boolean existsByStudentAndTask(User student, Task task);
}
