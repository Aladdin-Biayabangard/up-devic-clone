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
import com.team.updevic001.model.enums.CourseLevel;
import com.team.updevic001.model.enums.SortDirection;
import com.team.updevic001.model.enums.SortType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CourseService {

    ResponseCourseDto createCourse(CourseCategoryType courseCategoryType, CourseDto courseDto);

    ResponseCourseDto addTeacherToCourse(Long courseId, Long userId);

    void addToWishList(Long courseId);

    ResponseCourseDto updateCourse(Long courseId, CourseDto courseDto);

    void uploadCoursePhoto(Long courseId, MultipartFile multipartFile) throws IOException;

    void updateRatingCourse(Long courseId, int rating);

    ResponseFullCourseDto getCourse(Long courseId);

    CustomPage<ResponseCourseShortInfoDto> search(CourseSearchCriteria criteria,
                                                  CustomPageRequest request);

    CustomPage<ResponseCourseShortInfoDto> findCoursesByCategory(CourseCategoryType categoryType, CustomPageRequest request);

    List<ResponseCategoryDto> getCategories();

    CustomPage<ResponseCourseShortInfoDto> getWishList(CustomPageRequest request);

    List<ResponseCourseShortInfoDto> getMost5PopularCourses();

    void removeFromWishList(Long courseId);

    void deleteCourse(Long courseId);

}
