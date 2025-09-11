package com.team.updevic001.model.dtos.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTeacherDto {

    private String firstName;

    private String lastName;

    private String email;

    private String speciality;

    private Integer experienceYears;

    private String bio;

    private Set<String> socialLink;

    private Set<String> skills;

    private LocalDateTime hireDate;

    private String teacherPhoto;
}
