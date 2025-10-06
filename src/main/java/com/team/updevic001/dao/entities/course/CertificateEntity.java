package com.team.updevic001.dao.entities.course;

import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Table(name = "certificates")
public class CertificateEntity {

    @Id
    @Column(name = "credential_id", unique = true)
    String credentialId;

    String firstName;

    String lastName;

    String email;

    @Column(name = "issue_date")
    LocalDate issueDate;

    String issuedFor;

    String trainingName;

    String teacherName;

    @Column(columnDefinition = "TEXT")
    String description;

    String previewUrlHorizontal;

    String previewUrlVertical;

    @Column(name = "issuing_organization")
    String issuingOrganization;

    @Enumerated(STRING)
    CertificateStatus status;

    @Enumerated(STRING)
    CertificateType type;

    @CreationTimestamp
    LocalDateTime createdAt;

    String certificateUrl;

    @Column(name = "course_id")
    String courseId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    Set<String> tags = new HashSet<>();

    @Column(name = "user_id")
    Long userId;
}
