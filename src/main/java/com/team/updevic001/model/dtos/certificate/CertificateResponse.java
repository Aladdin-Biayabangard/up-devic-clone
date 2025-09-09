package com.team.updevic001.model.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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

    String issuingOrganization;

    CertificateType type;
}
