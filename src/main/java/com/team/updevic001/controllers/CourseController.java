package com.team.updevic001.controllers;

import com.team.updevic001.configuration.config.syncrn.RateLimit;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import com.team.updevic001.services.interfaces.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseServiceImpl;

    @Operation(
            summary = "Yeni kurs yaratmaq",
            description = "Yeni kurs əlavə etmək üçün istifadə olunur. Parametrlər: `courseCategoryType` query parametri və `CourseDto` JSON body-də göndərilir."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCourseDto createCourse(@RequestParam CourseCategoryType courseCategoryType,
                                          @RequestBody CourseDto courseDto) {
        return courseServiceImpl.createCourse(courseCategoryType, courseDto);
    }

    @Operation(
            summary = "Mövcud kursa müəllif əlavə etmək",
            description = "Mövcud kursa müəllif təyin etmək üçün `courseId` path param və `userId` query param göndərilir."
    )
    @PostMapping(path = "/{courseId}/teacher")
    @ResponseStatus(HttpStatus.OK)
    public ResponseCourseDto addTeacherToCourse(@PathVariable Long courseId,
                                                @RequestParam Long userId) {
        return courseServiceImpl.addTeacherToCourse(courseId, userId);
    }

    @Operation(
            summary = "Kursu wishlist-ə əlavə etmək",
            description = "İstifadəçinin wishlist-inə kurs əlavə etmək üçün `courseId` path param göndərilir."
    )
    @PostMapping(path = "/{courseId}/wish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addToWishList(@PathVariable Long courseId) {
        courseServiceImpl.addToWishList(courseId);
    }

    @Operation(
            summary = "Kurs şəkli yükləmək",
            description = "`multipart/form-data` ilə kurs şəkli yüklənir. Parametrlər: `courseId` query param və fayl `multipartFile`."
    )
    @PatchMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String uploadCoursePhoto(@RequestParam Long courseId,
                                    @RequestPart MultipartFile multipartFile) throws IOException {
        return courseServiceImpl.uploadCoursePhoto(courseId, multipartFile);
    }

    @Operation(
            summary = "Kursun ratingini yeniləmək",
            description = "Kursun `courseId` path param və `rating` query param göndərilərək kursun ratingi yenilənir."
    )
    @PatchMapping(path = "/{courseId}/rating")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String updateRatingCourse(@PathVariable Long courseId,
                                     @RequestParam int rating) {
        courseServiceImpl.updateRatingCourse(courseId, rating);
        return "Rating successfully added!";
    }

    @Operation(
            summary = "Kursun detallarını yeniləmək",
            description = "Kurs məlumatlarını güncəlləmək üçün `courseId` path param və `CourseDto` body göndərilir."
    )
    @PutMapping(path = "/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseCourseDto updateTeacherCourse(@PathVariable Long courseId,
                                                 @RequestBody CourseDto courseDto) {
        return courseServiceImpl.updateCourse(courseId, courseDto);
    }

    @Operation(
            summary = "Wishlist-dəki bütün kursları gətirmək",
            description = "İstifadəçinin wishlist-dəki bütün kursların qısa məlumatlarını əldə etmək üçün çağırılır."
    )
    @GetMapping(path = "/wish")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseCourseShortInfoDto> getWishList() {
        return courseServiceImpl.getWishList();
    }

    @Operation(
            summary = "Kursları sözə görə axtarmaq",
            description = "`keyword` query param göndərilərək uyğun kurslar axtarılır. Full-text search istifadə olunur."
    )
    @RateLimit
    @GetMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseCourseShortInfoDto> searchCourse(@RequestParam String keyword) {
        return courseServiceImpl.searchCourse(keyword);
    }

    @Operation(
            summary = "Kursları kriteriyalara görə axtarmaq",
            description = "`level`, `minPrice`, `maxPrice`, `courseCategoryType` query param-ləri ilə kurslar filtrelənir. Hamısı opsionaldır."
    )
    @GetMapping(path = "/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseCourseShortInfoDto> findCourseByCriteria(
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) CourseCategoryType courseCategoryType) {
        return courseServiceImpl.findCourseByCriteria(level, minPrice, maxPrice, courseCategoryType);
    }

}

