package com.team.updevic001.model.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CertificatePreviewUrls {

    String vertical;
    String horizontal;
}
