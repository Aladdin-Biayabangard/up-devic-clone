package com.team.updevic001.services.impl;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.mappers.CourseMapper;
import com.team.updevic001.criteria.CourseSearchCriteria;
import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCategoryDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.TeacherPermission;
import com.team.updevic001.model.enums.TeacherPrivileges;
import com.team.updevic001.services.interfaces.CourseService;
import com.team.updevic001.services.interfaces.FileLoadService;
import com.team.updevic001.services.interfaces.TeacherService;
import com.team.updevic001.specification.CourseSpecification;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.team.updevic001.model.enums.ExceptionConstants.ALREADY_EXISTS_EXCEPTION;
import static com.team.updevic001.model.enums.ExceptionConstants.COURSE_NOT_FOUND;
import static com.team.updevic001.model.enums.ExceptionConstants.FORBIDDEN_EXCEPTION;
import static com.team.updevic001.model.enums.ExceptionConstants.TEACHER_NOT_FOUND;
import static com.team.updevic001.utility.IDGenerator.normalizeString;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {


    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TeacherRepository teacherRepository;
    private final TeacherService teacherService;
    private final ModelMapper modelMapper;
    private final TeacherCourseRepository teacherCourseRepository;
    private final FileLoadService fileLoadServiceImpl;
    private final CourseRatingRepository courseRatingRepository;
    private final AuthHelper authHelper;
    private final WishListRepository wishListRepository;
    private final LessonRepository lessonRepository;
    private final DeleteService deleteService;

    @Override
    @Transactional
    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    public ResponseCourseDto createCourse(CourseCategoryType courseCategoryType,
                                          CourseDto courseDto) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        Course course = modelMapper.map(courseDto, Course.class);
        course.setId(normalizeString(course.getTitle()));
        course.setCourseCategoryType(courseCategoryType);
        course.setHeadTeacher(authenticatedTeacher);
        courseRepository.save(course);
        TeacherCourse teacherCourse = TeacherCourse.builder()
                .teacher(authenticatedTeacher)
                .course(course)
                .teacherPrivilege(TeacherPrivileges.HEAD_TEACHER)
                .build();
        teacherCourseRepository.save(teacherCourse);
        return courseMapper.courseDto(course);
    }

    @Override
    @Transactional
    public void addTeacherToCourse(String courseId, Long userId) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);

        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.ADD_TEACHER)) {
            throw new ForbiddenException(FORBIDDEN_EXCEPTION.getCode(), "User not allowed!");
        }

        Course course = findCourseById(courseId);

        Teacher newTeacher = teacherRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException(TEACHER_NOT_FOUND.getCode(),
                        TEACHER_NOT_FOUND.getMessage().formatted(userId)));

        boolean exists = teacherCourseRepository.existsByCourseIdAndTeacher(courseId, newTeacher);
        if (exists) {
            throw new AlreadyExistsException(ALREADY_EXISTS_EXCEPTION.getCode(), "Teacher already exists in this course!");
        } else {
            teacherCourseRepository.save(TeacherCourse.builder()
                    .course(course)
                    .teacher(newTeacher)
                    .teacherPrivilege(TeacherPrivileges.ASSISTANT_TEACHER)
                    .build());
        }
    }

    @Override
    public void addToWishList(String courseId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Course course = findCourseById(courseId);
        if (wishListRepository.existsWishListByCourseAndUser(course, authenticatedUser)) {
            throw new IllegalArgumentException("This course is already in your favorites.");
        }
        WishList wishList = WishList.builder()
                .course(course)
                .user(authenticatedUser)
                .build();
        wishListRepository.save(wishList);
    }

    @Override
    @Transactional
//    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    public void updateCourse(String courseId, CourseDto courseDto) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);

        Course findCourse = courseRepository
                .findById(courseId).orElseThrow(() -> new NotFoundException(COURSE_NOT_FOUND.getCode(),
                        COURSE_NOT_FOUND.getMessage().formatted(courseId)));

        modelMapper.map(courseDto, findCourse);
        courseRepository.save(findCourse);
        teacherCourse.setCourse(findCourse);
        teacherCourseRepository.save(teacherCourse);
    }

    public void uploadCoursePhoto(String courseId, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Multipart file is empty or null!");
        }
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException(COURSE_NOT_FOUND.getCode(),
                    COURSE_NOT_FOUND.getMessage().formatted(courseId));
        }
//        courseRepository.findProfilePhotoKeyBy(courseId).ifPresent(fileLoadServiceImpl::deleteFileFromAws);
        String photoOfWhat = "coursePhoto";
        FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, courseId, photoOfWhat);
        courseRepository.updateCourseFileInfo(courseId, fileUploadResponse.getKey(), fileUploadResponse.getUrl());
    }

    @Override
    public void updateRatingCourse(String courseId, int rating) {
        User user = authHelper.getAuthenticatedUser();
        Course course = findCourseById(courseId);
        CourseRating courseRating = courseRatingRepository.findCourseRatingByCourseAndUser(course, user)
                .orElseGet(() ->
                        CourseRating.builder()
                                .course(course)
                                .user(user)
                                .build());
        courseRating.setRating(rating);
        courseRatingRepository.save(courseRating);
        course.setRating(getAverageRating(course));
        courseRepository.save(course);
    }

    @Override
    @Cacheable(value = "courseSearchCache", key = "#courseId", unless = "#result==null", cacheManager = "cacheManager")
    public ResponseFullCourseDto getCourse(String courseId) {
        Course course = courseRepository.findCourseById(courseId).orElseThrow(() -> new NotFoundException(COURSE_NOT_FOUND.getCode(),
                COURSE_NOT_FOUND.getMessage().formatted(courseId)));
        return courseMapper.toFullResponse(course);
    }

    @Override
    public CustomPage<ResponseCourseShortInfoDto> search(CourseSearchCriteria criteria,
                                                         CustomPageRequest request) {

        Specification<Course> specification = Specification
                .where(CourseSpecification.hasLevel(criteria.getLevel()))
                .and(CourseSpecification.priceGreaterThanOrEqual(criteria.getMinPrice())
                        .and(CourseSpecification.priceLessThanOrEqual(criteria.getMaxPrice()))
                        .and(CourseSpecification.hasCategory(criteria.getCourseCategoryType()))
                        .and(CourseSpecification.hasName(criteria.getName())));

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Course> coursePage = courseRepository.findAll(specification, pageable);

        return new CustomPage<>(
                courseMapper.toCourseResponse(coursePage.getContent()),
                coursePage.getNumber(),
                coursePage.getSize()
        );
    }

    @Override
    public List<ResponseCategoryDto> getCategories() {
        return Arrays.stream(CourseCategoryType.values())
                .map(type -> {
                    long courCount = courseRepository.countByCourseCategoryType(type);
                    return new ResponseCategoryDto(type, courCount);
                })
                .toList();
    }

    @Override
    public CustomPage<ResponseCourseShortInfoDto> getWishList(CustomPageRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Page<Course> courseByUser = wishListRepository.findCourseByUser(authenticatedUser, pageable);
        return new CustomPage<>(
                courseMapper.toCourseResponse(courseByUser.getContent()),
                courseByUser.getNumber(),
                courseByUser.getSize()
        );
    }

    @Override
    public List<ResponseCourseShortInfoDto> getMost5PopularCourses() {
        List<Course> top5ByOrderByRatingDesc = courseRepository.findTop5ByOrderByRatingDesc();
        return courseMapper.toCourseResponse(top5ByOrderByRatingDesc);
    }

    @Override
    public void removeFromWishList(String courseId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        wishListRepository.deleteWishListByCourseIdAndUser(courseId, authenticatedUser);
    }

    @Override
    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    @Transactional
    public void deleteCourse(String courseId) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);
        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.DELETE_COURSE)) {
            throw new ForbiddenException(FORBIDDEN_EXCEPTION.getCode(), "User not allowed!");
        }
        List<String> allLessonIdsByCourseId = lessonRepository.findAllLessonIdsByCourseId(courseId);
        deleteService.deleteCourseAndReferencedData(courseId, allLessonIdsByCourseId, authenticatedTeacher.getUser());
    }

    public Course findCourseById(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(COURSE_NOT_FOUND.getCode(),
                        COURSE_NOT_FOUND.getMessage().formatted(courseId)));
    }

    private double getAverageRating(Course course) {
        List<CourseRating> ratings = courseRatingRepository.findByCourse(course);
        return ratings.stream()
                .mapToInt(CourseRating::getRating)
                .average()
                .orElse(0.0);
    }

    protected TeacherCourse validateAccess(String courseId, Teacher authenticatedTeacher) {
        return teacherCourseRepository.findByCourseIdAndTeacher(courseId, authenticatedTeacher)
                .orElseThrow(() -> new ForbiddenException(FORBIDDEN_EXCEPTION.getCode(), "User not allowed!"));
    }
}
