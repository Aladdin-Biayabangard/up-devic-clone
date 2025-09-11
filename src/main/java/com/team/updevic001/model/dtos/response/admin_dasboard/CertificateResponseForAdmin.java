package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertificateResponseForAdmin {

    private String fullName;

    private String trainingName;

    private LocalDateTime createdAt;

    private LocalDate issueDate;

    private String certificateUrl;

    private String certificatePhotoUrl;
}
