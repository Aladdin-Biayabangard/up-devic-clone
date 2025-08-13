package com.team.updevic001.model.dtos.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherMainInfo {

    int totalCourseCount;
    int totalStudentCount;
    BigDecimal balance;

}
