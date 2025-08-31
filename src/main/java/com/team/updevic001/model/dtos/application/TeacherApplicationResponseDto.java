package com.team.updevic001.model.dtos.application;

import com.team.updevic001.model.enums.ApplicationStatus;
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
public class TeacherApplicationResponseDto {

    UUID id;

    String fullName;

    String email;

    String teachingField;

    String linkedinProfile;

    String githubProfile;

    String portfolio;

    String additionalInfo;

    String phoneNumber;

    LocalDateTime createdAt;

    ApplicationStatus status;

    String resultMessage;

    LocalDateTime completedAt;
}
