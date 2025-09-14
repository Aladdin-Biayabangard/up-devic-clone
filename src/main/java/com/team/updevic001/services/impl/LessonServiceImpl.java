package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.Lesson;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserLessonStatus;
import com.team.updevic001.dao.repositories.LessonRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserLessonStatusRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.services.interfaces.FileLoadService;
import com.team.updevic001.services.interfaces.LessonService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.team.updevic001.exceptions.ExceptionConstants.FORBIDDEN_EXCEPTION;
import static com.team.updevic001.exceptions.ExceptionConstants.LESSON_NOT_FOUND;
import static com.team.updevic001.utility.IDGenerator.normalizeString;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;
    private final CourseServiceImpl courseServiceImpl;
    private final FileLoadService fileLoadService;
    private final com.team.updevic001.model.mappers.LessonMapper lessonMapper;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final AuthHelper authHelper;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final DeleteService deleteService;


    @Override
    @Transactional
    public void assignLessonToCourse(String courseId, LessonDto lessonDto, MultipartFile multipartFile) throws Exception {
        var teacher = authHelper.getAuthenticatedUser();

        courseServiceImpl.validateAccess(courseId, teacher);

        var lesson = modelMapper.map(lessonDto, Lesson.class);
        lesson.setId(normalizeString(lesson.getTitle()));
        var course = courseServiceImpl.findCourseById(courseId);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            lesson.setCourse(course);
            String videoOfWhat = "lessonVideo";
            FileUploadResponse fileUploadResponse = fileLoadService.uploadFileWithEncode(multipartFile, lesson.getId(), videoOfWhat);
            lesson.setVideoUrl(fileUploadResponse.getUrl());
            lesson.setVideoKey(fileUploadResponse.getKey());
            lesson.setTeacher(teacher);
            lessonRepository.save(lesson);
        }
    }

    @Override
    @Transactional
    public void updateLessonInfo(String lessonId, LessonDto lessonDto) {
        var teacher = authHelper.getAuthenticatedUser();
        var lesson = findLessonById(lessonId);
        if (!lesson.getTeacher().equals(teacher)) {
            throw new ForbiddenException(FORBIDDEN_EXCEPTION.getCode(), FORBIDDEN_EXCEPTION.getMessage());
        }
        modelMapper.map(lessonDto, lesson);
    }

    @Override
    @Transactional
    public void uploadLessonPhoto(String lessonId, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Multipart file is empty or null!");
        }
        if (!lessonRepository.existsById(lessonId)) {
            throw new NotFoundException(LESSON_NOT_FOUND.getCode(), LESSON_NOT_FOUND.getMessage().formatted(lessonId));
        }
        String photoOfWhat = "lessonPhoto";
        FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, lessonId, photoOfWhat);
        lessonRepository.updateCourseFileInfo(lessonId, fileUploadResponse.getKey(), fileUploadResponse.getUrl());
    }

    @Override
    public List<ResponseLessonShortInfoDto> getShortLessonsByCourse(String courseId) {
        var lessons = lessonRepository.findByCourseIdOrderByCreatedAtAsc(courseId);
        return lessons.isEmpty() ? List.of() : lessonMapper.toShortLesson(lessons);
    }

    public ResponseLessonDto getFullLessonByLessonId(String lessonId) {
        var teacher = authHelper.getAuthenticatedUser();
        var lesson = findLessonById(lessonId);

        boolean exists = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(lesson.getCourse(), teacher);
        boolean isTeacher = lessonRepository.existsLessonByTeacherAndLesson(teacher, lesson);

        if (!exists && !isTeacher) {
            throw new IllegalArgumentException("ACCESS_DENIED");
        }

        lesson.setVideoUrl(fileLoadService.getFileUrlWithEncode(lesson.getVideoKey()));
        markLessonAsWatched(teacher, lesson);

        return lessonMapper.toDto(lesson);
    }


    @Override
    @Transactional
    public void deleteLesson(String lessonId) {
        var teacher = authHelper.getAuthenticatedUser();
        var lesson = findLessonById(lessonId);
        courseServiceImpl.validateAccess(lesson.getCourse().getId(), teacher);
        deleteService.deleteLessonAndReferencedData(lesson, lessonId);
    }

    @Override
    public Lesson findLessonById(String lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(LESSON_NOT_FOUND.getCode(),
                        LESSON_NOT_FOUND.getMessage().formatted(lessonId)));

    }

    private void markLessonAsWatched(User user, Lesson lesson) {
        UserLessonStatus userLessonStatus = new UserLessonStatus();
        userLessonStatus.setUser(user);
        userLessonStatus.setLesson(lesson);
        userLessonStatus.setWatched(true);
        userLessonStatusRepository.save(userLessonStatus);
    }

}
