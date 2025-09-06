package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.StudentTask;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    long countAllByTaskIdIn(List<Long> taskIds);

    boolean existsStudentTaskByCompletedAndStudentAndTask(Boolean completed, User student, Task task);

   Optional<StudentTask> findByStudentAndTask(User student, Task task);
}
