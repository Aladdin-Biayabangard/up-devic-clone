package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.model.dtos.response.video.LessonVideoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LessonService {

    ResponseLessonDto assignLessonToCourse(Long courseId, LessonDto lessonDto, MultipartFile file) throws Exception;

    ResponseLessonDto updateLessonInfo(Long lessonId, LessonDto lessonDto);

    String uploadLessonPhoto(Long lessonId, MultipartFile multipartFile) throws IOException;

    List<ResponseLessonShortInfoDto> getShortLessonsByCourse(Long courseId);

    ResponseLessonDto getFullLessonByLessonId(Long lessonId);


//    List<ResponseLessonDto> getTeacherLessons();


    LessonVideoResponse getVideo(Long lessonId);


    Lesson findLessonById(Long lessonId);

    void deleteLesson(Long lessonId);

    void deleteTeacherLessons();


}
