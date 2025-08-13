package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.CourseMapper;
import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.request.CourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCategoryDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.enums.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final TestResultRepository testResultRepository;
    private final UserLessonStatusRepository userLessonStatusRepository;
    private final UserCourseFeeRepository userCourseFeeRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final CertificateRepository certificateRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    public ResponseCourseDto createCourse(CourseCategoryType courseCategoryType,
                                          CourseDto courseDto) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        Course course = modelMapper.map(courseDto, Course.class);
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
    public ResponseCourseDto addTeacherToCourse(Long courseId, Long userId) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        Course course = findCourseById(courseId);
        Teacher newTeacher = teacherRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(Teacher.class));

        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);

        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.ADD_TEACHER)) {
            throw new ForbiddenException("NOT_ALLOWED");
        }

        Optional<TeacherCourse> newTeacherToCourseCheck = teacherCourseRepository.findByCourseIdAndTeacher(courseId, newTeacher);

        if (newTeacherToCourseCheck.isPresent()) {
            throw new AlreadyExistsException("TEACHER_ALREADY_EXISTS_IN_THIS_COURSE");
        } else {
            teacherCourseRepository.save(TeacherCourse.builder()
                    .course(course)
                    .teacher(newTeacher)
                    .teacherPrivilege(TeacherPrivileges.ASSISTANT_TEACHER)
                    .build());
        }
        return courseMapper.courseDto(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    public ResponseCourseDto updateCourse(Long courseId, CourseDto courseDto) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        Course findCourse = courseRepository
                .findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found!"));

        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);
        modelMapper.map(courseDto, findCourse);

        courseRepository.save(findCourse);
        teacherCourse.setCourse(findCourse);
        teacherCourseRepository.save(teacherCourse);
        return courseMapper.courseDto(findCourse);
    }

    public String uploadCoursePhoto(Long courseId, MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            courseRepository.findProfilePhotoKeyBy(courseId).ifPresent(fileLoadServiceImpl::deleteFileFromAws);
            String photoOfWhat = "coursePhoto";
            FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, courseId, photoOfWhat);
            courseRepository.updateCourseFileInfo(courseId, fileUploadResponse.getKey(), fileUploadResponse.getUrl());
            return fileUploadResponse.getUrl();
        }
        throw new IllegalArgumentException("Multipart file is empty or null!");
    }

    @Override
    @CacheEvict(value = {"courseSearchCache", "courseSortCache"}, allEntries = true)
    @Transactional
    public void deleteCourse(Long courseId) {
        Teacher authenticatedTeacher = teacherService.getAuthenticatedTeacher();
        TeacherCourse teacherCourse = validateAccess(courseId, authenticatedTeacher);
        if (!teacherCourse.getTeacherPrivilege().hasPermission(TeacherPermission.DELETE_COURSE)) {
            throw new ForbiddenException("NOT_ALLOWED");
        }

        List<Long> allLessonIdsByCourseId = lessonRepository.findAllLessonIdsByCourseId(courseId);
        deleteCourseAndReferencedData(courseId, allLessonIdsByCourseId, authenticatedTeacher.getUser());
    }

    public TeacherCourse validateAccess(Long courseId, Teacher authenticatedTeacher) {
        return teacherCourseRepository.findByCourseIdAndTeacher(courseId, authenticatedTeacher).orElseThrow(() -> new ForbiddenException("NOT_ALLOWED"));
    }

    @Override
    @Cacheable(
            value = "courseSearchCache",
            key = "T(java.util.Objects).hash(#level, #minPrice, #maxPrice, #courseCategoryType)",
            unless = "#result==null",
            cacheManager = "cacheManager"
    )
    public List<ResponseCourseShortInfoDto> findCourseByCriteria(CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice, CourseCategoryType courseCategoryType) {
        Specification<Course> specification = Specification.where(
                        CourseSpecification.hasLevel(level))
                .and(CourseSpecification.priceGreaterThanOrEqual(minPrice))
                .and(CourseSpecification.priceLessThanOrEqual(maxPrice))
                .and(CourseSpecification.hasCategory(courseCategoryType));
        List<Course> courses = courseRepository.findAll(specification);
        return courseMapper.toCourseResponse(courses);
    }

    @Override
    @Cacheable(value = "courseSearchCache", key = "#keyword", unless = "#result == null", cacheManager = "cacheManager")
    public List<ResponseCourseShortInfoDto> searchCourse(String keyword) {
        List<Course> courses = courseRepository.searchCoursesByKeyword(keyword);
        return !courses.isEmpty() ? courseMapper.toCourseResponse(courses) : List.of();
    }

    @Override
    @Cacheable(value = "courseSearchCache", key = "#courseId", unless = "#result==null", cacheManager = "cacheManager")
    public ResponseFullCourseDto getCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<Lesson> lessons = lessonRepository.findLessonByCourseId(courseId);
        List<Comment> comments = commentRepository.findCommentByCourseId(courseId);
        return courseMapper.toFullResponse(course, lessons, comments);
    }

    @Override
    @Cacheable(value = "courseSearchCache", unless = "#result.isEmpty()", cacheManager = "cacheManager")
    public List<ResponseCourseShortInfoDto> getCourses(Long id, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Course> courses = courseRepository.findByIdGreaterThanOrderByIdAscRatingDescCreatedAtDesc(id, pageable);
        return !courses.isEmpty() ? courseMapper.toCourseResponse(courses) : List.of();
    }

    @Override
    public void addToWishList(Long courseId) {
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
    public List<ResponseCourseShortInfoDto> getWishList() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        List<Course> courseByUser = wishListRepository.findCourseByUser(authenticatedUser);
        return courseMapper.toCourseResponse(courseByUser);
    }

    @Override
    public List<ResponseCategoryDto> getCategories() {
        return Arrays.stream(CourseCategoryType.values())
                .map(type -> {
                    List<Course> courses = courseRepository.findCourseByCourseCategoryType(type);
                    return new ResponseCategoryDto(type, courses.size());
                })
                .toList();
    }

    @Override
    public List<ResponseCourseShortInfoDto> findCoursesByCategory(CourseCategoryType categoryType) {
        List<Course> courseByCourseCategoryType = courseRepository.findCourseByCourseCategoryType(categoryType);
        return !courseByCourseCategoryType.isEmpty() ? courseMapper.toCourseResponse(courseByCourseCategoryType) : new ArrayList<>();
    }

    @Override
    public void updateRatingCourse(Long courseId, int rating) {
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
    public List<ResponseCourseShortInfoDto> getMost5PopularCourses() {
        List<Course> top5ByOrderByRatingDesc = courseRepository.findTop5ByOrderByRatingDesc();
        return courseMapper.toCourseResponse(top5ByOrderByRatingDesc);
    }

    @Override
    @Cacheable(
            value = "courseSortCache",
            key = "'sort:' + #sortBy.name() + ':dir:' + #direction.name()",
            unless = "#result == null or #result.isEmpty()",
            cacheManager = "cacheManager"
    )
    public List<ResponseCourseShortInfoDto> filterAndSortCourses(SortType sortBy, SortDirection direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.name()); // ASC or DESC
        Sort sort = Sort.by(sortDirection, sortBy.name());
        List<Course> courses = courseRepository.findAll(sort);
        return courses.isEmpty() ? List.of() : courseMapper.toCourseResponse(courses);
    }

    public Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("COURSE_NOT_FOUND"));
    }

    @Override
    public void removeFromWishList(Long courseId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        wishListRepository.deleteWishListByCourseIdAndUser(courseId, authenticatedUser);
    }

    double getAverageRating(Course course) {
        List<CourseRating> ratings = courseRatingRepository.findByCourse(course);
        return ratings.stream()
                .mapToInt(CourseRating::getRating)
                .average()
                .orElse(0.0);
    }

    void deleteCourseAndReferencedData(Long courseId, List<Long> lessonIds, User user) {

        wishListRepository.deleteWishListByCourseIdAndUser(courseId, user);

        userLessonStatusRepository.deleteUserLessonStatusByLessonsId(lessonIds);
        commentRepository.deleteCommentsByLessonsId(lessonIds);

        commentRepository.deleteCommentsByCourseId(courseId);
        lessonRepository.deleteAllById(lessonIds);

        certificateRepository.deleteCertificateByCourseId(courseId);
        courseRatingRepository.deleteRatingByCourseId(courseId);
        studentCourseRepository.deleteStudentCourseByCourseId(courseId);
        taskRepository.deleteTaskByCourseId(courseId);
        teacherCourseRepository.deleteTeacherCourseByCourseId(courseId);
        userCourseFeeRepository.deleteCourseFeeByCourseId(courseId);
        testResultRepository.deleteAllByCourseId(courseId);
        courseRatingRepository.deleteAllByCourseId(courseId);
        courseRepository.deleteById(courseId);
    }
}
