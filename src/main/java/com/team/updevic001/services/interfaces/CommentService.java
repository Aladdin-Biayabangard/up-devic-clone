package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CommentDto;
import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;

public interface CommentService {

    void addCommentToCourse(String courseId, CommentDto commentDto);

    void addCommentToLesson(String lessonId, CommentDto commentDto);

    void updateComment(Long commentId, CommentDto commentDto);

    CustomPage<ResponseCommentDto> getCourseComment(String courseId, CustomPageRequest request);

    CustomPage<ResponseCommentDto> getLessonComment(String lessonId, CustomPageRequest request);

    void deleteComment(Long commentId);
}
