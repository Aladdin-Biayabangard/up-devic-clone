package com.team.updevic001.model.dtos.application;

import com.team.updevic001.model.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationSearchDto {

    private String email;
    private String fullName;
    private String teachingField;
    private String phone;
    private ApplicationStatus status;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
}
