package com.team.updevic001.model.dtos.response.teacher;

import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.enums.Specialty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTeacherWithCourses {

    private String firstName;
    private String lastName;
    private Specialty speciality;
    private Integer experienceYears;
    private List<ResponseCourseDto> courseDtoS;
}
