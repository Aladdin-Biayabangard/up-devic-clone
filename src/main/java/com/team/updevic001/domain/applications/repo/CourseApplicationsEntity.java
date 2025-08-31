package com.team.updevic001.domain.applications.repo;

import com.team.updevic001.domain.applications.domain.ApplicationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "applications")
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseApplicationsEntity {

    @Id
    UUID id;

    UUID courseId;

    String email;

    String fullName;

    String phone;

    LocalDateTime createdAt;

    ApplicationStatus status;

    String resultMessage;

    LocalDateTime completedAt;
}
