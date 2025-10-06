package com.team.updevic001.model.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CertificateResponse {

    String credentialId;

    String fullName;

    String issueDate;

    String issuedFor;

    String trainingName;

    String teacherName;

    String issuingOrganization;

    CertificateType type;

    String description;

    Set<String> tags;
}
