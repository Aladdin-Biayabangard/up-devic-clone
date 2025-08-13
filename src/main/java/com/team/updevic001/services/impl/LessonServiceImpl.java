package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.LessonMapper;
import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.CommentRepository;
import com.team.updevic001.dao.repositories.LessonRepository;
import com.team.updevic001.dao.repositories.UserCourseFeeRepository;
import com.team.updevic001.dao.repositories.UserLessonStatusRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.PaymentStatusException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.dtos.response.video.LessonVideoResponse;
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
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ResponseLessonDto assignLessonToCourse(Long courseId, LessonDto lessonDto, MultipartFile multipartFile) throws Exception {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        Lesson lesson = modelMapper.map(lessonDto, Lesson.class);

        Course course = courseServiceImpl.findCourseById(courseId);

        TeacherCourse teacherCourse = courseServiceImpl.validateAccess(courseId, authenticatedTeacher);

        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.ADD_LESSON)) {
            throw new ForbiddenException("NOT_ALLOWED");
        }


        if (multipartFile != null && !multipartFile.isEmpty()) {
            lesson.setCourse(course);
            Lesson savedLesson = lessonRepository.save(lesson);
            lessonRepository.findLessonVideoKeyByLessonId(savedLesson.getId()).ifPresent(fileLoadService::deleteFileFromAws);
            String videoOfWhat="lessonVideo";
            FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, lesson.getId(),videoOfWhat);
//            File file = videoServiceImpl.convertToFile(multipartFile);
//            String videoDurationInSeconds = videoServiceImpl.getVideoDurationInSeconds(file);
//            lesson.setDuration(videoDurationInSeconds);
            lesson.setVideoUrl(fileUploadResponse.getUrl());
            lesson.setTeacher(authenticatedTeacher);
            lessonRepository.save(lesson);
        }

        return modelMapper.map(lesson, ResponseLessonDto.class);
    }

    @Override
    @Transactional
    public ResponseLessonDto updateLessonInfo(Long lessonId, LessonDto lessonDto) {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        Lesson lesson = findLessonById(lessonId);
        if (!lesson.getTeacher().getId().equals(authenticatedTeacher.getId())) {
            throw new ForbiddenException("NOT_ALLOWED_UPDATE_LESSON");
        }
        modelMapper.map(lessonDto, lesson);
        return modelMapper.map(lesson, ResponseLessonDto.class);
    }

    @Override
    public String uploadLessonPhoto(Long lessonId, MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            lessonRepository.findLessonPhotoKeyByLessonId(lessonId).ifPresent(fileLoadService::deleteFileFromAws);
            String photoOfWhat="lessonPhoto";
            FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, lessonId,photoOfWhat);
            lessonRepository.updateCourseFileInfo(lessonId, fileUploadResponse.getKey(), fileUploadResponse.getUrl());
            return fileUploadResponse.getUrl();
        }
        throw new IllegalArgumentException("Multipart file is empty or null!");
    }

    @Override
    public List<ResponseLessonShortInfoDto> getShortLessonsByCourse(Long courseId) {
        List<Lesson> lessons = lessonRepository.findLessonByCourseId(courseId);
        return lessons.isEmpty() ? List.of() : lessonMapper.toShortLesson(lessons);
    }

    public ResponseLessonDto getFullLessonByLessonId(Long lessonId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Lesson lesson = findLessonById(lessonId);
        boolean exists = userCourseFeeRepository.existsUserCourseFeeByCourseAndUser(lesson.getCourse(), authenticatedUser);
        if (exists) {
            markLessonAsWatched(authenticatedUser, lesson);
            List<Comment> comments = commentRepository.findCommentByLessonId(lessonId);
            return lessonMapper.toDto(lesson,comments);
        } else {
            throw new PaymentStatusException("The user has not paid for the course. ");
        }
    }


    @Override
    public LessonVideoResponse getVideo(Long lessonId) {
        Lesson lesson = findLessonById(lessonId);
        return new LessonVideoResponse(
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getDuration()
        );
    }

    @Override
    public Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found these Id"));
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
        Lesson lesson = findLessonById(lessonId);
        TeacherCourse teacherCourse = courseServiceImpl.validateAccess(lesson.getCourse().getId(), authenticatedTeacher);
        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.DELETE_LESSON) || !lesson.getTeacher().getId().equals(authenticatedTeacher.getId())) {
            throw new ForbiddenException("NOT_ALLOWED_DELETE_LESSON");
        }
       deleteLessonAndReferencedData(lesson,lessonId);
    }

    @Override
    @Transactional
    public void deleteTeacherLessons() {
        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();

        List<Lesson> lessons = lessonRepository.findLessonsByTeacherId(authenticatedTeacher.getId());
        lessons.forEach(lesson -> fileLoadService.deleteFileFromAws(lesson.getPhotoKey()));
        lessonRepository.deleteAll(lessons);
    }

    public void markLessonAsWatched(User user, Lesson lesson) {
        UserLessonStatus userLessonStatus = new UserLessonStatus();
        userLessonStatus.setUser(user);
        userLessonStatus.setLesson(lesson);
        userLessonStatus.setWatched(true);
        userLessonStatusRepository.save(userLessonStatus);
    }

    public void deleteLessonAndReferencedData(Lesson lesson,Long lessonId){
        fileLoadService.deleteFileFromAws(lesson.getVideoKey());
        fileLoadService.deleteFileFromAws(lesson.getPhotoKey());


        userLessonStatusRepository.deleteUserLessonStatusByLessonsId(List.of(lessonId));
        commentRepository.deleteCommentsByLessonsId(List.of(lessonId));

        lessonRepository.delete(lesson);
    }
}

//    @Override
//    public List<ResponseLessonDto> getTeacherLessons() {
//        Teacher authenticatedTeacher = teacherServiceImpl.getAuthenticatedTeacher();
//        log.info("Getting teacher lessons. Teacher ID: {}", authenticatedTeacher.getId());
//
//        List<Lesson> lessons = lessonRepository.findLessonsByTeacherId(authenticatedTeacher.getId());
//
//        log.info("Retrieved {} lessons for teacher ID: {}", lessons.size(), authenticatedTeacher.getId());
//        return lessonMapper.toDto(lessons);
//    }
