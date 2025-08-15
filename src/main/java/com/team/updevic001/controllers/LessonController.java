package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.LessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.services.interfaces.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonServiceImpl;

    @Operation(
            summary = "Kursa dərs əlavə etmək",
            description = "Yeni dərsi mövcud kursa əlavə etmək üçün istifadə olunur. Parametrlər: `courseId` path param, `lessonDto` form-data və dərs faylı `file` multipart-file."
    )
    @PostMapping(path = "/{courseId}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseLessonDto assignLessonToCourse(
            @PathVariable Long courseId,
            @ModelAttribute("lesson") LessonDto lessonDto,
            @RequestPart("file") final MultipartFile file) throws Exception {
        return lessonServiceImpl.assignLessonToCourse(courseId, lessonDto, file);
    }

    @Operation(
            summary = "Dərsin məlumatlarını yeniləmək",
            description = "`lessonId` path param və `LessonDto` body göndərilərək dərs məlumatları güncəllənir."
    )
    @PutMapping("/{lessonId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseLessonDto updateLessonInfo(@PathVariable Long lessonId,
                                              @Valid @RequestBody LessonDto lessonDto) {
        return lessonServiceImpl.updateLessonInfo(lessonId, lessonDto);
    }

    @Operation(
            summary = "Dərsin şəkilini yükləmək",
            description = "Dərsin şəkli `lessonId` query param və `multipartFile` ilə yüklənir."
    )
    @PatchMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String uploadLessonPhoto(@RequestParam Long lessonId,
                                    @RequestPart MultipartFile multipartFile) throws IOException {
        return lessonServiceImpl.uploadLessonPhoto(lessonId, multipartFile);
    }

    @Operation(
            summary = "Kursdakı bütün dərslərin qısa məlumatlarını gətirmək",
            description = "`courseId` path param göndərilərək kursa aid bütün dərslərin qısa məlumatları əldə edilir."
    )
    @GetMapping(path = "/{courseId}/lesson-short")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseLessonShortInfoDto> getLessonsByCourse(@PathVariable Long courseId) {
        return lessonServiceImpl.getShortLessonsByCourse(courseId);
    }

    @Operation(
            summary = "Dərsin tam məlumatını əldə etmək",
            description = "`lessonId` path param göndərilərək dərsin bütün məlumatları əldə edilir."
    )
    @GetMapping(path = "/{lessonId}/lesson")
    @ResponseStatus(HttpStatus.OK)
    public ResponseLessonDto getFullLessonByLessonId(@PathVariable Long lessonId) {
        return lessonServiceImpl.getFullLessonByLessonId(lessonId);
    }

    @Operation(
            summary = "Dərsi silmək",
            description = "`lessonId` path param göndərilərək dərs silinir."
    )
    @DeleteMapping(path = "/{lessonId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLesson(@PathVariable Long lessonId) {
        lessonServiceImpl.deleteLesson(lessonId);
    }

    @Operation(
            summary = "Müəllifin bütün dərslərini silmək",
            description = "Müəllifin bütün dərsləri silinir. Parametr tələb olunmur."
    )
    @DeleteMapping(path = "/lessons/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTeacherLessons() {
        lessonServiceImpl.deleteTeacherLessons();
    }
}


//    @Operation(summary = "See all of the teacher's lessons")
//    @GetMapping(path = "teacher-lessons")
//    public ResponseEntity<List<ResponseLessonDto>> getTeacherLessons() {
//        List<ResponseLessonDto> teacherLessons = lessonServiceImpl.getTeacherLessons();
//        return ResponseEntity.ok(teacherLessons);
//    }


//    @GetMapping("/{lessonId}/video")
//    public ResponseEntity<LessonVideoResponse> getLessonWithVideo(
//            @PathVariable String lessonId) {
//
//        LessonVideoResponse response = lessonServiceImpl.getVideo(lessonId);
//        return ResponseEntity.ok(response);
//    }
