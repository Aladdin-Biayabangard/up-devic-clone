package com.team.updevic001.model.dtos.response.student;

import com.team.updevic001.dao.entities.Certificate;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStudentDto extends ResponseUserDto {

    private String studentNumber;

    private Certificate certificate;

    private LocalDateTime enrolledDate;

}
