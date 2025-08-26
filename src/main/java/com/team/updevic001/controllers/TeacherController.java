package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;
import com.team.updevic001.services.interfaces.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherServiceImpl;

    @Operation(summary = "View the teacher courses.")
    @GetMapping(path = "/courses")
    public ResponseEntity<List<ResponseCourseShortInfoDto>> getTeacherAndCourses() {
        List<ResponseCourseShortInfoDto> teacherAndCourses = teacherServiceImpl.getTeacherAndRelatedCourses();
        return ResponseEntity.ok(teacherAndCourses);
    }

    @Operation(summary = "Muellimin kurs telebe sayini ve balansini gosterir ")
    @GetMapping(path = "/info")
    public ResponseEntity<TeacherMainInfo> getInfo() {
        TeacherMainInfo info = teacherServiceImpl.getInfo();
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Muellimin profiline baxmaq")
    @GetMapping(path = "/{teacherId}/profile")
    public ResponseTeacherDto getProfile(@PathVariable Long teacherId) {
        return teacherServiceImpl.getTeacherProfile(teacherId);
    }

    @Operation(summary = "Muellimin card melumatlari")
    @GetMapping(path = "/{teacherId}/info")
    public TeacherNameDto getInfo(@PathVariable Long teacherId) {
        return teacherServiceImpl.getTeacherShortInfo(teacherId);
    }

    @Operation(summary = "Delete the teacher")
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<String> deleteTeacher(@PathVariable Long userId) {
        teacherServiceImpl.deleteTeacher(userId);
        return ResponseEntity.ok("Teacher deleted!");
    }

    @Operation(summary = "Delete all teachers")
    @DeleteMapping(path = "/all")
    public ResponseEntity<String> deleteAllTeachers() {
        teacherServiceImpl.deleteAllTeachers();
        return ResponseEntity.ok("All teacher deleted!");
    }

    @Operation(summary = "Muellimleri axtarir")
    @GetMapping(path = "/search")
    public ResponseEntity<List<ResponseTeacherDto>> searchTeacher(@RequestParam String keyword) {
        List<ResponseTeacherDto> responseTeachers = teacherServiceImpl.searchTeacher(keyword);
        return ResponseEntity.ok(responseTeachers);
    }
}