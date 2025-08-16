package com.team.updevic001.services.interfaces;

import com.team.updevic001.criteria.CourseSearchCriteria;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCategoryDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import com.team.updevic001.model.enums.CourseCategoryType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {

    ResponseCourseDto createCourse(CourseCategoryType courseCategoryType, CourseDto courseDto);

    ResponseCourseDto addTeacherToCourse(String courseId, Long userId);

    void addToWishList(String courseId);

    ResponseCourseDto updateCourse(String courseId, CourseDto courseDto);

    void uploadCoursePhoto(String courseId, MultipartFile multipartFile) throws IOException;

    void updateRatingCourse(String courseId, int rating);

    ResponseFullCourseDto getCourse(String courseId);

    CustomPage<ResponseCourseShortInfoDto> search(CourseSearchCriteria criteria,
                                                  CustomPageRequest request);

    CustomPage<ResponseCourseShortInfoDto> findCoursesByCategory(CourseCategoryType categoryType, CustomPageRequest request);

    List<ResponseCategoryDto> getCategories();

    CustomPage<ResponseCourseShortInfoDto> getWishList(CustomPageRequest request);

    List<ResponseCourseShortInfoDto> getMost5PopularCourses();

    void removeFromWishList(String courseId);

    void deleteCourse(String courseId);

}
