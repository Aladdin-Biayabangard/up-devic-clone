package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.services.interfaces.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;


    @Operation(summary = "Unenroll a student from a course")
    @DeleteMapping("/unenroll")
    public void unenrollFromCourse(@RequestParam String courseId) {
        studentService.unenrollUserFromCourse(courseId);
    }

    @Operation(summary = "Get a student's course information")
    @GetMapping
    public ResponseCourseShortInfoDto getStudentCourse(@RequestParam String courseId) {
        return studentService.getStudentCourse(courseId);
    }

    @Operation(summary = "Get all courses of a student")
    @GetMapping("/courses")
    public List<ResponseCourseShortInfoDto> getStudentCourses() {
        return studentService.getStudentCourses();
    }

    @Operation(summary = "API request to become a teacher!")
    @GetMapping(path = "/for-teacher")
    public ResponseEntity<String> requestToBecameTeacher() {
        return ResponseEntity.ok("https://forms.gle/GersS1t7jwena3Dz9");
    }
}

