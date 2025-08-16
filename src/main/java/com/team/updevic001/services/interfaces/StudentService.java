package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;

import java.util.List;

public interface StudentService {

    void enrollInCourse(String courseId, User user);

    void unenrollUserFromCourse(String courseId);

    ResponseCourseShortInfoDto getStudentCourse(String courseId);

    List<ResponseCourseShortInfoDto> getStudentCourses();

}