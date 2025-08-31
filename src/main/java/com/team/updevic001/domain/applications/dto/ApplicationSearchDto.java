package com.team.updevic001.domain.applications.dto;

import com.team.updevic001.domain.applications.domain.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationSearchDto {

    private String email;
    private String fullName;
    private String phone;
    private String message;
    private ApplicationStatus status;
    private Boolean success;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
}
