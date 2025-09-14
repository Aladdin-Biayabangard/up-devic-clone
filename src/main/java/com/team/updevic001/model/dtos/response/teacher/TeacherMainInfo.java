package com.team.updevic001.model.dtos.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherMainInfo {

    private Long teacherId;
    private String teacherName;
    private String email;
}
