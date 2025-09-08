package com.team.updevic001.model.dtos.certificate;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CertificateRequestDto {
    PersonDto person;
    String email;
    LocalDate issueDate;
    LocalDate expireDate;
    String issuedFor;
    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    String description;
    List<String> skills;
    String issuingOrganization;
    CertificateStatus status;
    String postMessage;
    String emailMessage;
    CertificateType type;
}
