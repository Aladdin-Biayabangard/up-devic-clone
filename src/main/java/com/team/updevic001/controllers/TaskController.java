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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskServiceImpl;

    @PostMapping(path = "/courses/{courseId}")
    public ResponseEntity<String> createTask(@PathVariable String courseId,
                                             @RequestBody TaskDto taskDto) {
        taskServiceImpl.createTask(courseId, taskDto);
        return ResponseEntity.ok("Task successfully created!");
    }

    @PostMapping(path = "/{taskId}/courses/{courseId}")
    public ResponseEntity<String> checkAnswer(@PathVariable String courseId,
                                              @PathVariable Long taskId,
                                              @RequestBody AnswerDto answerDto) {
        taskServiceImpl.checkAnswer(courseId, taskId, answerDto);
        return ResponseEntity.ok("Correct answer!");
    }

    @GetMapping(path = "/courses/{courseId}")
    public ResponseEntity<List<ResponseTaskDto>> getTasks(@PathVariable String courseId) {
        List<ResponseTaskDto> tasks = taskServiceImpl.getTasks(courseId);
        return ResponseEntity.ok(tasks);
    }
}
