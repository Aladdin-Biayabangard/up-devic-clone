package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;
import com.team.updevic001.services.interfaces.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentServiceImpl;

    @Operation(summary = "Kursa coment yazmaq")
    @PostMapping(path = "{courseId}/course-comment")
    public ResponseCommentDto addCommentToCourse(@PathVariable Long courseId,
                                                                 @RequestBody CommentDto comment) {
        return commentServiceImpl.addCommentToCourse(courseId, comment);
    }

    @Operation(summary = "Derse comment yazmaq")
    @PostMapping(path = "{lessonId}/lesson-comment")
    public ResponseCommentDto addCommentToLesson(@PathVariable Long lessonId,
                                                                 @RequestBody CommentDto comment) {
        return commentServiceImpl.addCommentToLesson(lessonId, comment);
    }

    @Operation(summary = "Commenti yenilemek üçün")
    @PutMapping(path = "/{commentId}")
    public ResponseCommentDto updateComment(@PathVariable Long commentId,
                                                            @RequestBody CommentDto commentDto) {
        return commentServiceImpl.updateComment(commentId, commentDto);
    }

    @Operation(summary = "Kursun bütün kommnentleri")
    @GetMapping(path = "/{courseId}/course")
    public List<ResponseCommentDto> getCourseComment(@PathVariable Long courseId) {
        return commentServiceImpl.getCourseComment(courseId);
    }

    @Operation(summary = "Dersin butun kommentleri")
    @GetMapping(path = "/{lessonId}/lesson")
    public List<ResponseCommentDto> getLessonComment(@PathVariable Long lessonId) {
        return commentServiceImpl.getLessonComment(lessonId);
    }

    @Operation(summary = "Commenti silmek")
    @DeleteMapping(path = "/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentServiceImpl.deleteComment(commentId);
    }

}
