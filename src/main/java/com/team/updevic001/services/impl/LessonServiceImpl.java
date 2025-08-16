package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.LessonMapper;
import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.LessonRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserLessonStatusRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.enums.TeacherPermission;
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

import static com.team.updevic001.utility.IDGenerator.normalizeString;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final TeacherServiceImpl teacherServiceImpl;
    private final ModelMapper modelMapper;
    private final CourseServiceImpl courseServiceImpl;
    private final FileLoadService fileLoadService;
    private final LessonMapper lessonMapper;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final AuthHelper authHelper;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final DeleteService deleteService;


    @Override
    @Transactional
    public void assignLessonToCourse(String courseId, LessonDto lessonDto, MultipartFile multipartFile) throws Exception {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        TeacherCourse teacherCourse = courseServiceImpl.validateAccess(courseId, authenticatedTeacher);
        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.ADD_LESSON)) {
            throw new ForbiddenException("NOT_ALLOWED");
        }

        Lesson lesson = modelMapper.map(lessonDto, Lesson.class);
        lesson.setId(normalizeString(lesson.getTitle()));
        Course course = courseServiceImpl.findCourseById(courseId);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            lesson.setCourse(course);
            String videoOfWhat = "lessonVideo";
            FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, lesson.getId(), videoOfWhat);
            lesson.setVideoUrl(fileUploadResponse.getUrl());
            lesson.setTeacher(authenticatedTeacher);
            lessonRepository.save(lesson);
        }
    }

    @Override
    @Transactional
    public void updateLessonInfo(String lessonId, LessonDto lessonDto) {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        Lesson lesson = findLessonById(lessonId);
        if (!lesson.getTeacher().equals(authenticatedTeacher)) {
            throw new ForbiddenException("NOT_ALLOWED_UPDATE_LESSON");
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
            throw new ResourceNotFoundException("Lesson not found these Id: " + lessonId);
        }
        String photoOfWhat = "lessonPhoto";
        FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, lessonId, photoOfWhat);
        lessonRepository.updateCourseFileInfo(lessonId, fileUploadResponse.getKey(), fileUploadResponse.getUrl());
    }

    @Override
    public List<ResponseLessonShortInfoDto> getShortLessonsByCourse(String courseId) {
        List<Lesson> lessons = lessonRepository.findLessonByCourseId(courseId);
        return lessons.isEmpty() ? List.of() : lessonMapper.toShortLesson(lessons);
    }

    public ResponseLessonDto getFullLessonByLessonId(String lessonId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Lesson lesson = findLessonById(lessonId);
        boolean exists = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(lesson.getCourse(), authenticatedUser);
        if (exists || lessonRepository.existsLessonByTeacherAndLesson(teacherServiceImpl.getAuthenticatedTeacher(), lesson)) {
            markLessonAsWatched(authenticatedUser, lesson);
            //   List<Comment> comments = commentRepository.findCommentByLessonId(lessonId);
            return lessonMapper.toDto(lesson);
        } else {
            throw new IllegalArgumentException("ACCESS_DENIED");
        }
    }

    @Override
    @Transactional
    public void deleteLesson(String lessonId) {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        Lesson lesson = findLessonById(lessonId);
        TeacherCourse teacherCourse = courseServiceImpl.validateAccess(lesson.getCourse().getId(), authenticatedTeacher);
        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.DELETE_LESSON) || !lesson.getTeacher().getId().equals(authenticatedTeacher.getId())) {
            throw new ForbiddenException("NOT_ALLOWED_DELETE_LESSON");
        }
        deleteService.deleteLessonAndReferencedData(lesson, lessonId);
    }

    @Override
    public Lesson findLessonById(String lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found these Id"));
    }

    private void markLessonAsWatched(User user, Lesson lesson) {
        UserLessonStatus userLessonStatus = new UserLessonStatus();
        userLessonStatus.setUser(user);
        userLessonStatus.setLesson(lesson);
        userLessonStatus.setWatched(true);
        userLessonStatusRepository.save(userLessonStatus);
    }

}
