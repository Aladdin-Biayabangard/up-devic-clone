package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;

import java.util.List;

public interface StudentService {

    void enrollInCourse(Long courseId, User user);

    void unenrollUserFromCourse(Long courseId);

    ResponseCourseShortInfoDto getStudentCourse(Long courseId);

    List<ResponseCourseShortInfoDto> getStudentCourses();

//    List<ResponseFullCourseDto> getStudentLessons();

}