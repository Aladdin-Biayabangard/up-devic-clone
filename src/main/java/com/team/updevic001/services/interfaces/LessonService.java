package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LessonService {

    void assignLessonToCourse(String courseId, LessonDto lessonDto, MultipartFile file) throws Exception;

    void updateLessonInfo(String lessonId, LessonDto lessonDto);

    void uploadLessonPhoto(String lessonId, MultipartFile multipartFile) throws IOException;

    ResponseLessonDto getFullLessonByLessonId(String lessonId);

    List<ResponseLessonShortInfoDto> getShortLessonsByCourse(String courseId);

    void deleteLesson(String lessonId);

    Lesson findLessonById(String lessonId);

}
