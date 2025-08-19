package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;
import com.team.updevic001.services.interfaces.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentServiceImpl;

    @Operation(summary = "Kursa coment yazmaq")
    @PostMapping(path = "courses{courseId}")
    @ResponseStatus(CREATED)
    public ResponseCommentDto addCommentToCourse(@PathVariable String courseId,
                                                 @RequestBody CommentDto comment) {
        return commentServiceImpl.addCommentToCourse(courseId, comment);
    }

    @Operation(summary = "Derse comment yazmaq")
    @PostMapping(path = "/lessons{lessonId}")
    @ResponseStatus(CREATED)
    public ResponseCommentDto addCommentToLesson(@PathVariable String lessonId,
                                                 @RequestBody CommentDto comment) {
        return commentServiceImpl.addCommentToLesson(lessonId, comment);
    }

    @Operation(summary = "Commenti yenilemek üçün")
    @PutMapping(path = "/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void updateComment(@PathVariable Long commentId,
                                            @RequestBody CommentDto commentDto) {
         commentServiceImpl.updateComment(commentId, commentDto);
    }

    @Operation(summary = "Kursun bütün kommnentleri")
    @GetMapping(path = "courses/{courseId}")
    public CustomPage<ResponseCommentDto> getCourseComment(@PathVariable String courseId, CustomPageRequest request) {
        return commentServiceImpl.getCourseComment(courseId, request);
    }

    @Operation(summary = "Dersin butun kommentleri")
    @GetMapping(path = "lessons/{lessonId}")
    public CustomPage<ResponseCommentDto> getLessonComment(@PathVariable String lessonId, CustomPageRequest request) {
        return commentServiceImpl.getLessonComment(lessonId, request);
    }

    @Operation(summary = "Commenti silmek")
    @DeleteMapping(path = "/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentServiceImpl.deleteComment(commentId);
    }

}
