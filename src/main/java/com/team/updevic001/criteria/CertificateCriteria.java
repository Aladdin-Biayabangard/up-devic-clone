package com.team.updevic001.criteria;

import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertificateCriteria {
    private String email;
    private String trainingName;
    private CertificateStatus status;
    private CertificateType type;
    private LocalDate dateFrom;
    private LocalDate toDate;
}
