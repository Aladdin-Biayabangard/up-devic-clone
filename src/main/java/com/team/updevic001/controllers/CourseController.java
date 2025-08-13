package com.team.updevic001.controllers;

import com.team.updevic001.configuration.config.syncrn.RateLimit;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCategoryDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import com.team.updevic001.model.enums.SortDirection;
import com.team.updevic001.model.enums.SortType;
import com.team.updevic001.services.interfaces.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Kurs yaratmaq")
    @PostMapping
    public ResponseEntity<ResponseCourseDto> createCourse(@RequestParam CourseCategoryType courseCategoryType, @RequestBody CourseDto courseDto) {
        ResponseCourseDto teacherCourse = courseServiceImpl.createCourse(courseCategoryType, courseDto);
        return new ResponseEntity<>(teacherCourse, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{courseId}/teacher")
    public ResponseEntity<ResponseCourseDto> addTeacherToCourse(@PathVariable Long courseId,
                                                                @RequestParam Long userId) {
        ResponseCourseDto responseTeacherWithCourses = courseServiceImpl.addTeacherToCourse(courseId, userId);
        return ResponseEntity.ok(responseTeacherWithCourses);
    }

    @Operation(summary = "Kursu beyenilen kurslara elave etmek")
    @PostMapping(path = "/{courseId}/wish")
    public ResponseEntity<Void> addToWishList(@PathVariable Long courseId) {
        courseServiceImpl.addToWishList(courseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Kursa şəklini düzəltmək")
    @PatchMapping(path = "photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCoursePhoto(@RequestParam Long courseId,
                                                    @RequestPart MultipartFile multipartFile) throws IOException {
        String photoUrl = courseServiceImpl.uploadCoursePhoto(courseId, multipartFile);
        return ResponseEntity.ok(photoUrl);
    }

    @Operation(summary = "Kursun ratingini yenilemek")
    @PatchMapping(path = "{courseId}/rating")
    public ResponseEntity<String> updateRatingCourse(@PathVariable Long courseId,
                                                     @RequestParam int rating) {
        courseServiceImpl.updateRatingCourse(courseId, rating);
        return new ResponseEntity<>("Rating successfully added!", HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Kursun detallarini yenilemek")
    @PutMapping(path = "/{courseId}")
    public ResponseEntity<ResponseCourseDto> updateTeacherCourse(@PathVariable Long courseId,
                                                                 @RequestBody CourseDto courseDto) {
        return new ResponseEntity<>(courseServiceImpl.updateCourse(courseId, courseDto), HttpStatus.OK);
    }

    @Operation(summary = "İstifadəcinin Butun beyenilen kurslarinin qisa melumatlarini getirir")
    @GetMapping(path = "wish")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> getWishList() {
        List<ResponseCourseShortInfoDto> wishList = courseServiceImpl.getWishList();
        return ResponseEntity.ok(wishList);
    }


    @Operation(summary = "Kursu her hansi soze uygun axtarmaq ")
    @RateLimit
    @GetMapping(path = "/search")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> searchCourse(@RequestParam String keyword) {
        List<ResponseCourseShortInfoDto> courses = courseServiceImpl.searchCourse(keyword);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Kursu her hansi kriteryaya uygun axtarmaq")
    @GetMapping(path = "/criteria")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> findCourseByCriteria(@RequestParam(required = false) CourseLevel level,
                                                                                 @RequestParam(required = false) BigDecimal minPrice,
                                                                                 @RequestParam(required = false) BigDecimal maxPrice,
                                                                                 @RequestParam(required = false) CourseCategoryType courseCategoryType) {
        List<ResponseCourseShortInfoDto> coursesByCriteria = courseServiceImpl.findCourseByCriteria(level, minPrice, maxPrice, courseCategoryType);
        return ResponseEntity.ok(coursesByCriteria);
    }

    @GetMapping(path = "/sort")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> filterAndSortCourses(@RequestParam SortType sortBy,
                                                                                 @RequestParam SortDirection direction) {
        List<ResponseCourseShortInfoDto> courses = courseServiceImpl.filterAndSortCourses(sortBy, direction);
        return ResponseEntity.ok(courses);
    }

    @GetMapping(path = "/{courseId}/full")
    public ResponseEntity<ResponseFullCourseDto> getCourse(@PathVariable Long courseId) {
        ResponseFullCourseDto course = courseServiceImpl.getCourse(courseId);
        return ResponseEntity.ok(course);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> getCourses(Long id, int limit) {
        List<ResponseCourseShortInfoDto> courses = courseServiceImpl.getCourses(id, limit);
        return ResponseEntity.ok(courses);
    }

    @GetMapping(path = "categories")
    public ResponseEntity<List<ResponseCategoryDto>> getCategories() {
        List<ResponseCategoryDto> categories = courseServiceImpl.getCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping(path = "most-popular")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> getMost4PopularCourses() {
        return ResponseEntity.ok(courseServiceImpl.getMost5PopularCourses());
    }

    @GetMapping(path = "/short")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> findCoursesByCategory(@RequestParam CourseCategoryType categoryType) {
        List<ResponseCourseShortInfoDto> category = courseServiceImpl.findCoursesByCategory(categoryType);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping(path = "/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseServiceImpl.deleteCourse(courseId);
        return ResponseEntity.ok("Course successfully deleted!");
    }

    @DeleteMapping(path = "{courseId}/wish")
    public ResponseEntity<Void> removeFromWishList(@PathVariable Long courseId) {
        courseServiceImpl.removeFromWishList(courseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}

