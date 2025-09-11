package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.services.interfaces.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherServiceImpl;

    @Operation(summary = "View the teacher courses.")
    @GetMapping(path = "/courses")
    public List<ResponseCourseShortInfoDto> getTeacherAndCourses() {
        return teacherServiceImpl.getTeacherAndRelatedCourses();
    }

    @GetMapping(path = "/{teacherId}/courses")
    public List<ResponseCourseShortInfoDto> getTeacherAndRelatedCourses(@PathVariable Long teacherId) {
        return teacherServiceImpl.getTeacherAndRelatedCourses(teacherId);
    }

    @Operation(summary = "Muellimin profiline baxmaq")
    @GetMapping(path = "/{teacherId}/profile")
    public ResponseTeacherDto getProfile(@PathVariable Long teacherId) {
        return teacherServiceImpl.getTeacherProfile(teacherId);
    }

    @Operation(summary = "Muellimleri axtarir")
    @GetMapping(path = "/search")
    public List<ResponseTeacherDto> searchTeacher(@RequestParam String keyword) {
        return teacherServiceImpl.searchTeacher(keyword);
    }
}
