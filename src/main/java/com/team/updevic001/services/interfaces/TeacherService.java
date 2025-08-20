package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;

import java.util.List;

public interface TeacherService {

    TeacherMainInfo getInfo();

    List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses();

    List<ResponseTeacherDto> searchTeacher(String keyword);

    Teacher getAuthenticatedTeacher();

    ResponseTeacherDto getTeacherProfile(Long teacherId);

    TeacherNameDto getTeacherShortInfo(Long teacherId);

    void deleteTeacher(Long teacherId);

    void deleteAllTeachers();

}
