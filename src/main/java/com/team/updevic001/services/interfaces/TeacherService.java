package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;

import java.util.List;

public interface TeacherService {

    List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses(Long teacherId);

    List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses();

    List<ResponseTeacherDto> searchTeacher(String keyword);

    ResponseTeacherDto getTeacherProfile(Long teacherId);

    TeacherNameDto getTeacherShortInfo(User teacher);

}
