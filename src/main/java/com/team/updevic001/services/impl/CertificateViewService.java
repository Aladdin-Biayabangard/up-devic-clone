package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.CertificateViewEntity;
import com.team.updevic001.dao.repositories.CertificateViewRepository;
import com.team.updevic001.model.dtos.certificate.CertificateViewDto;
import com.team.updevic001.model.dtos.certificate.CertificateViewPageResponse;
import com.team.updevic001.model.dtos.certificate.Platform;
import com.team.updevic001.model.dtos.certificate.PlatformStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateViewService {

    private final CertificateViewRepository certificateViewRepository;

    @Transactional
    public void incrementViewCount(String certificateId, Platform platform) {
        if (platform == null) platform = Platform.OTHER;

        Platform finalPlatform = platform;
        var view = certificateViewRepository
                .findByCertificateIdAndPlatform(certificateId, platform)
                .orElseGet(() -> CertificateViewEntity.builder()
                        .certificateId(certificateId)
                        .platform(finalPlatform)
                        .viewCount(0L)
                        .build());

        view.setViewCount(view.getViewCount() + 1);
        view = certificateViewRepository.save(view);

        CertificateViewDto.builder()
                .certificateId(view.getCertificateId())
                .platform(view.getPlatform())
                .viewCount(view.getViewCount())
                .build();
    }

    public List<CertificateViewDto> getViewsByCertificate(String certificateId) {
        return certificateViewRepository.findAllByCertificateId(certificateId)
                .stream()
                .map(view -> CertificateViewDto.builder()
                        .certificateId(view.getCertificateId())
                        .platform(view.getPlatform())
                        .viewCount(view.getViewCount())
                        .build())
                .toList();
    }

    public CertificateViewPageResponse getAllViews(Pageable pageable) {
        Page<CertificateViewEntity> page = certificateViewRepository.findAll(pageable);
        List<CertificateViewDto> views = page.stream()
                .map(view -> CertificateViewDto.builder()
                        .certificateId(view.getCertificateId())
                        .platform(view.getPlatform())
                        .viewCount(view.getViewCount())
                        .build())
                .toList();

        return new CertificateViewPageResponse(
                views,
                page.getTotalPages() - 1,
                page.getTotalElements(),
                page.hasNext()
        );
    }

    public List<PlatformStatsDto> getTotalViewsByPlatform() {
        List<Object[]> results = certificateViewRepository.getTotalViewsByPlatform();
        var total = results.stream().mapToLong(r -> (Long) r[1]).sum();

        return results.stream()
                .map(r -> PlatformStatsDto.builder()
                        .platform((Platform) r[0])
                        .count((Long) r[1])
                        .percentage(total == 0 ? 0.0 :
                                BigDecimal.valueOf(((double) (Long) r[1] / total) * 100.0)
                                        .setScale(1, RoundingMode.HALF_UP)
                                        .doubleValue())
                        .build())
                .toList();
    }
}