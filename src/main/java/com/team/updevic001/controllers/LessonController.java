package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.services.interfaces.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonServiceImpl;

    @Operation(summary = "Kursa dərs əlavə etmək")
    @PostMapping(path = "/courses/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    public void assignLessonToCourse(
            @PathVariable String courseId,
            @ModelAttribute("lesson") LessonDto lessonDto,
            @RequestPart("file") final MultipartFile file) throws Exception {
        lessonServiceImpl.assignLessonToCourse(courseId, lessonDto, file);
    }

    @Operation(summary = "Dərsin məlumatlarını yeniləmək")
    @PutMapping("/{lessonId}")
    @ResponseStatus(NO_CONTENT)
    public void updateLessonInfo(@PathVariable String lessonId,
                                 @Valid @RequestBody LessonDto lessonDto) {
        lessonServiceImpl.updateLessonInfo(lessonId, lessonDto);
    }

    @Operation(summary = "Dərsin şəkilini yükləmək")
    @PatchMapping(value = "/{lessonId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void uploadLessonPhoto(@PathVariable String lessonId,
                                  @RequestPart MultipartFile multipartFile) throws IOException {
        lessonServiceImpl.uploadLessonPhoto(lessonId, multipartFile);
    }

    @Operation(summary = "Kursdakı bütün dərslərin qısa məlumatlarını gətirmək")
    @GetMapping(path = "/courses/{courseId}")
    public List<ResponseLessonShortInfoDto> getLessonsByCourse(@PathVariable String courseId) {
        return lessonServiceImpl.getShortLessonsByCourse(courseId);
    }

    @Operation(summary = "Dərsin tam məlumatını əldə etmək")
    @GetMapping(path = "/{lessonId}")
    public ResponseLessonDto getFullLessonByLessonId(@PathVariable String lessonId) {
        return lessonServiceImpl.getFullLessonByLessonId(lessonId);
    }

    @Operation(summary = "Dərsi silmək")
    @DeleteMapping(path = "/{lessonId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteLesson(@PathVariable String lessonId) {
        lessonServiceImpl.deleteLesson(lessonId);
    }

}
