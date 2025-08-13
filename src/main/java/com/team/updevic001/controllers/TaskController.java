package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.AnswerDto;
import com.team.updevic001.model.dtos.request.TaskDto;
import com.team.updevic001.model.dtos.response.task.ResponseTaskDto;
import com.team.updevic001.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskServiceImpl;

    @PostMapping(path = "student/{courseId}/lesson/create-task")
    public ResponseEntity<String> createTask(@PathVariable Long courseId,
                                             @RequestBody TaskDto taskDto) {
        taskServiceImpl.createTask(courseId, taskDto);
        return ResponseEntity.ok("Task successfully created!");
    }

    @PostMapping(path = "student/{courseId}/lesson/{taskId}/check")
    public ResponseEntity<String> checkAnswer(@PathVariable Long courseId,
                                              @PathVariable Long taskId,
                                              @RequestBody AnswerDto answerDto) {
        taskServiceImpl.checkAnswer(courseId, taskId, answerDto);
        return ResponseEntity.ok("Correct answer!");
    }

    @GetMapping(path = "course/{courseId}")
    public ResponseEntity<List<ResponseTaskDto>> getTasks(@PathVariable Long courseId) {
        List<ResponseTaskDto> tasks = taskServiceImpl.getTasks(courseId);
        return ResponseEntity.ok(tasks);
    }
}
