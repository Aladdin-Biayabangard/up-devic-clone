package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.CourseMapper;
import com.team.updevic001.configuration.mappers.TeacherMapper;
import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.*;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.services.interfaces.TeacherService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final AuthHelper authHelper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;
    private final SocialLinkRepository socialLinkRepository;


    @Override
    public List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses() {
        Teacher teacher = getAuthenticatedTeacher();
        List<Course> courses = courseRepository.findCourseByHeadTeacher(teacher);
        return courses.stream().map(courseMapper::toCourseResponse).toList();
    }

    @Override
    public TeacherMainInfo getInfo() {
        Teacher teacher = getAuthenticatedTeacher();
        List<Long> allCourseIdsByTeacher = teacherCourseRepository.findAllCourseIdsByTeacher(teacher);
        int courseCount = allCourseIdsByTeacher.size();
        int studentCount = studentCourseRepository.countAllStudentsByCourseIds(allCourseIdsByTeacher);
        return new TeacherMainInfo(courseCount, studentCount, teacher.getBalance());
    }

    @Override
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = validateTeacherAndAccess(teacherId);
        teacherRepository.delete(teacher);
    }

    @Override
    public void deleteAllTeachers() {
        teacherRepository.deleteAll();
        teacherRepository.resetAutoIncrement();
    }

    private Teacher validateTeacherAndAccess(Long teacherId) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Teacher teacher = findTeacherById(teacherId);

        boolean isOwner = teacherRepository.existsTeacherByUserId(authenticatedUser.getId());
        boolean isAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(userRole -> userRole.getName().equals(Role.ADMIN));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("NOT_ALLOWED");
        }

        return teacher;
    }

    public List<ResponseTeacherDto> searchTeacher(String keyword) {
        List<Teacher> teachers = teacherRepository.searchTeacher(keyword);

        if (teachers.isEmpty()) {
            return List.of();
        }

        List<User> users = teachers.stream()
                .map(Teacher::getUser)
                .toList();

        List<UserProfile> userProfiles = userProfileRepository.findByUsers(users);

        Map<Long, UserProfile> userIdToProfile = userProfiles.stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), Function.identity()));

        List<SocialLink> socialLinks = socialLinkRepository.findByUserProfiles(userProfiles);

        Map<Long, List<String>> profileIdToLinks = socialLinks.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getUserProfile().getId(),
                        Collectors.mapping(SocialLink::getLink, Collectors.toList())
                ));

        return teachers.stream()
                .map(teacher -> {
                    User user = teacher.getUser();
                    UserProfile profile = userIdToProfile.get(user.getId());
                    List<String> links = profileIdToLinks.getOrDefault(profile.getId(), List.of());
                    return teacherMapper.toTeacherDto(teacher, links);
                })
                .toList();
    }


    public Teacher getAuthenticatedTeacher() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        Optional<Teacher> teacher = teacherRepository.findByUserId(authenticatedUser.getId());
        if (teacher.isEmpty()) {
            throw new ForbiddenException("NOT_ALLOWED");
        }
        return teacher.get();
    }

    private Teacher findTeacherById(Long teacherID) {
        return teacherRepository.findById(teacherID)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found this id: " + teacherID));
    }
}