package com.team.updevic001.model.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CertificateDto {
    String email;
    String credentialId;
    PersonDto person;
    LocalDate issueDate;
    LocalDate expireDate;
    String issuedFor;
    String description;
    CertificatePreviewUrls previewUrls;
    List<String> skills;
    String issuingOrganization;
    CertificateStatus status;
    LocalDateTime createdAt;
    String postMessage;
    String emailMessage;
    CertificateType type;
}
