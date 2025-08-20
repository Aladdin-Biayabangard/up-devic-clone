package com.team.updevic001.controllers;

import com.team.updevic001.criteria.CourseSearchCriteria;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCategoryDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.services.interfaces.CourseService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Yeni kurs yaratmaq")
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseCourseDto createCourse(@RequestParam CourseCategoryType courseCategoryType,
                                          @RequestBody CourseDto courseDto) {
        return courseService.createCourse(courseCategoryType, courseDto);
    }

    @Operation(summary = "Mövcud kursa müəllif əlavə etmək")
    @PostMapping(path = "/{courseId}/teachers/{userId}")
    public void addTeacherToCourse(@PathVariable String courseId,
                                   @PathVariable Long userId) {
        courseService.addTeacherToCourse(courseId, userId);
    }

    @Operation(summary = "Kursu wishlist-ə əlavə etmək")
    @PostMapping(path = "/{courseId}/wish")
    @ResponseStatus(NO_CONTENT)
    public void addToWishList(@PathVariable String courseId) {
        courseService.addToWishList(courseId);
    }

    @Operation(summary = "Kursun detallarını yeniləmək")
    @PutMapping(path = "/{courseId}")
    @ResponseStatus(NO_CONTENT)
    public void updateCourse(@PathVariable String courseId,
                             @RequestBody CourseDto courseDto) {
        courseService.updateCourse(courseId, courseDto);
    }

    @Operation(summary = "Kurs şəkli yükləmək")
    @PatchMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCoursePhoto(@RequestParam String courseId,
                                  @RequestPart MultipartFile multipartFile) throws IOException {
        courseService.uploadCoursePhoto(courseId, multipartFile);
    }

    @Operation(summary = "Kursun ratingini yeniləmək")
    @PatchMapping(path = "/{courseId}/rating")
    @ResponseStatus(NO_CONTENT)
    public void updateRatingCourse(@PathVariable String courseId,
                                   @RequestParam int rating) {
        courseService.updateRatingCourse(courseId, rating);
    }

    @Operation(summary = "Kursun uzerine vurduqda full data gelir")
    @GetMapping("{courseId}")
    public ResponseFullCourseDto getCourse(@PathVariable String courseId) {
        return courseService.getCourse(courseId);
    }

    @Operation(summary = "Kursları kriteriyalara görə axtarmaq ve kriteriya olmasa butun kurslari getirmek")
    @GetMapping("/search")
    public CustomPage<ResponseCourseShortInfoDto> searchCourses(
            CourseSearchCriteria criteria,
            CustomPageRequest request) {
        return courseService.search(criteria, request);
    }

    @Operation(summary = "Butun categoryleri getirir.")
    @GetMapping("categories")
    public List<ResponseCategoryDto> getCategories() {
        return courseService.getCategories();
    }

    @Operation(summary = "Wishlist-dəki bütün kursları gətirmək")
    @GetMapping(path = "/wish")
    public CustomPage<ResponseCourseShortInfoDto> getWishList(CustomPageRequest request) {
        return courseService.getWishList(request);
    }

    @Operation(summary = "5 eded popular kursları gətirmək")
    @GetMapping(path = "popular-courses")
    public List<ResponseCourseShortInfoDto> getMost5PopularCourses() {
        return courseService.getMost5PopularCourses();
    }

    @DeleteMapping(path = "/{courseId}/wish")
    void removeFromWishList(@PathVariable String courseId) {
        courseService.removeFromWishList(courseId);
    }

    @Operation(summary = "Kursu wish listden silir")
    @DeleteMapping(path = "{courseId}")
    public void deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
    }
}

