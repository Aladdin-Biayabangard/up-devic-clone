package com.team.updevic001.model.dtos.certificate;

import lombok.Builder;

@Builder
public record CertificateViewDto(
        String certificateId,
        Platform platform,
        Long viewCount
) {}