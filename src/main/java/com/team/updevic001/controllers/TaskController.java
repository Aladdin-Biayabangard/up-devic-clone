package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.AnswerDto;
import com.team.updevic001.model.dtos.request.TaskDto;
import com.team.updevic001.model.dtos.response.task.ResponseSubmission;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;
import com.team.updevic001.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskServiceImpl;

    @PostMapping(path = "/courses/{courseId}")
    public void createTask(@PathVariable String courseId,
                           @RequestBody TaskDto taskDto) {
        taskServiceImpl.createTask(courseId, taskDto);
    }

    @PostMapping(path = "/{taskId}/courses/{courseId}")
    public void checkAnswer(@PathVariable String courseId,
                            @PathVariable Long taskId,
                            @RequestBody AnswerDto answerDto) {
        taskServiceImpl.checkAnswer(courseId, taskId, answerDto);
    }


    @GetMapping(path = "/courses/{courseId}")
    public List<ResponseTaskDto> getTasks(@PathVariable String courseId) {
        return taskServiceImpl.getTasks(courseId);
    }

    @GetMapping("/{courseId}/submissions")
    public List<ResponseSubmission> getSubmissionTasks(@PathVariable String courseId) {
        return taskServiceImpl.getSubmissionTasks(courseId);
    }

    @DeleteMapping(path = "/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskServiceImpl.deleteTask(taskId);
    }
}
