package com.team.updevic001.model.dtos.response.teacher;

import com.team.updevic001.model.enums.Specialty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTeacherDto {

    private String firstName;

    private String lastName;

    private String email;

    private Specialty speciality;

    private Integer experienceYears;

    private List<String> socialLink;

    private LocalDateTime hireDate;

}
