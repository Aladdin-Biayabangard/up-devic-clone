package com.team.updevic001.services.impl;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.mappers.CourseMapper;
import com.team.updevic001.model.mappers.TeacherMapper;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.services.interfaces.TeacherService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.team.updevic001.model.enums.ExceptionConstants.TEACHER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final AuthHelper authHelper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    @Override
    public List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        List<Course> courses = courseRepository.findCourseByTeacher(authenticatedUser);
        return courses.stream().map(courseMapper::toCourseResponse).toList();
    }

    @Override
    public TeacherMainInfo getInfo() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        var courseIds = courseRepository.findCourseIdsByTeacher(authenticatedUser);
        int studentCount = studentCourseRepository.countAllStudentsByCourseIds(courseIds);
        return new TeacherMainInfo(courseIds.size(), studentCount);
    }

    public ResponseTeacherDto getTeacherProfile(Long userId) {
        User user = userService.fetchUserById(userId);
        if (!userService.existsByUserAndRole(user, Role.TEACHER)) {
            throw new NotFoundException(TEACHER_NOT_FOUND.getCode(), TEACHER_NOT_FOUND.getMessage().formatted(userId));
        }
        UserProfile teacherProfile = userProfileRepository.findTeacherWithRelations(user);
        return teacherMapper.toTeacherDto(user, teacherProfile);
    }

    public TeacherNameDto getTeacherShortInfo(Long teacherId) {
        return userRepository.findTeacherNameByUserId(teacherId);
    }

    public List<ResponseTeacherDto> searchTeacher(String keyword) {
        // 1. Teacher-ları axtar
        List<User> teachers = userRepository.searchTeacher(keyword);

        if (teachers.isEmpty()) {
            return List.of();
        }

        List<UserProfile> userProfiles = userProfileRepository.findByUsers(teachers);

        Map<Long, UserProfile> userIdToProfile = userProfiles.stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), Function.identity()));

        return teachers.stream()
                .map(teacher -> {
                    UserProfile profile = userIdToProfile.get(teacher.getId());
                    return teacherMapper.toTeacherDto(teacher, profile);
                })
                .toList();
    }

}