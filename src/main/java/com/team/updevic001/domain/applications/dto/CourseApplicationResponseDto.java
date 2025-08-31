package com.team.updevic001.domain.applications.dto;

import com.team.updevic001.domain.applications.domain.ApplicationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseApplicationResponseDto {
    UUID id;

    UUID courseId;

    String email;

    String fullName;

    String phone;

    String resultMessage;

    ApplicationStatus status;

    LocalDateTime createdAt;

    LocalDateTime completedAt;
}
