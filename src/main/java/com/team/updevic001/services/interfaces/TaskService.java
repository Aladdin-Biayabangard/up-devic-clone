package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.request.AnswerDto;
import com.team.updevic001.model.dtos.request.TaskDto;
import com.team.updevic001.model.dtos.response.task.ResponseSubmission;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;

import java.util.List;

public interface TaskService {

    void createTask(String courseId, TaskDto taskDto);

    void checkAnswer(String courseId, Long taskId, AnswerDto answerDto);

    List<ResponseTaskDto> getTasks(String courseId);

    List<ResponseSubmission> getSubmissionTasks(String courseId);

    void deleteTask(Long taskId);
}



