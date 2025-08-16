package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;

public interface CommentService {

    ResponseCommentDto addCommentToCourse(Long courseId, CommentDto commentDto);

    ResponseCommentDto addCommentToLesson(Long lessonId, CommentDto commentDto);

    ResponseCommentDto updateComment(Long commentId, CommentDto commentDto);

    CustomPage<ResponseCommentDto> getCourseComment(Long courseId, CustomPageRequest request);

    CustomPage<ResponseCommentDto> getLessonComment(Long lessonId, CustomPageRequest request);

    void deleteComment(Long commentId);
}
