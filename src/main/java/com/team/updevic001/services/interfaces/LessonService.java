package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LessonService {

    void assignLessonToCourse(Long courseId, LessonDto lessonDto, MultipartFile file) throws Exception;

    void updateLessonInfo(Long lessonId, LessonDto lessonDto);

    void uploadLessonPhoto(Long lessonId, MultipartFile multipartFile) throws IOException;

    ResponseLessonDto getFullLessonByLessonId(Long lessonId);

    List<ResponseLessonShortInfoDto> getShortLessonsByCourse(Long courseId);

    void deleteLesson(Long lessonId);

    Lesson findLessonById(Long lessonId);

}
