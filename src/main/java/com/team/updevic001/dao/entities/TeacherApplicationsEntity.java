package com.team.updevic001.dao.entities;

import com.team.updevic001.model.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
import org.hibernate.annotations.CreationTimestamp;

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
public class TeacherApplicationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(length = 100, nullable = false)
    String fullName; // Adınız

    @Column(length = 100, nullable = false)
    String email; // Qeydiyyat email

    @Column(length = 100, nullable = false)
    String teachingField; // Tədris etmək istədiyi sahə

    @Column(length = 255, nullable = false)
    String linkedinProfile; // Linkedin profil linki

    @Column(length = 255, nullable = false)
    String githubProfile; // GitHub profil linki

    @Column(length = 255)
    String portfolio; // Portfolio (optional)

    @Column(columnDefinition = "TEXT")
    String additionalInfo; // Əlavə məlumat

    String phoneNumber;

    @CreationTimestamp
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    ApplicationStatus status;

    String resultMessage;

    LocalDateTime completedAt;
}
