package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;

import java.util.List;

public interface CommentService {

    ResponseCommentDto addCommentToCourse(Long courseId, CommentDto commentDto);

    ResponseCommentDto addCommentToLesson(Long lessonId, CommentDto commentDto);

    ResponseCommentDto updateComment(Long commentId, CommentDto commentDto);

    List<ResponseCommentDto> getCourseComment(Long courseId);

    List<ResponseCommentDto> getLessonComment(Long lessonId);

    void deleteComment(Long commentId);
}
