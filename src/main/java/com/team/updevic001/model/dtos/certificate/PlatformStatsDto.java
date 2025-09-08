package com.team.updevic001.model.dtos.certificate;

import lombok.Builder;

@Builder
public record PlatformStatsDto(
        Platform platform,
        Long count,
        Double percentage
) {}