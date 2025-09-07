package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.StudentTask;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {

    long countAllByTaskIdIn(List<Long> taskIds);

    boolean existsStudentTaskByCompletedAndStudentAndTask(Boolean completed, User student, Task task);

    List<StudentTask> findByStudentAndTaskIn(User student, List<Task> tasks);

}
