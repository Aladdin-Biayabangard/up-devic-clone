package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.certificate.CertificateDto;
import com.team.updevic001.model.dtos.certificate.CertificatePreviewUrls;
import com.team.updevic001.model.dtos.certificate.CertificateRequestDto;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateViewDto;
import com.team.updevic001.model.dtos.certificate.CertificateViewPageResponse;
import com.team.updevic001.model.dtos.certificate.Platform;
import com.team.updevic001.model.dtos.certificate.PlatformStatsDto;
import com.team.updevic001.services.impl.CertificateService;
import com.team.updevic001.services.impl.CertificateViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateViewService certificateViewService;


    @GetMapping("/{id}")
    public CertificateDto getCertificate(@PathVariable String id,
                                         @RequestParam(required = false) Platform platform,
                                         Authentication authentication) {
        return certificateService.getCertificate(id, platform, authentication);
    }

    @GetMapping("/search")
    public Page<CertificateDto> getAllCertificatesOrSearchByQuery(@RequestParam(required = false) String query,
                                                                  @RequestParam CertificateStatus status,
                                                                  Pageable pageable) {
        return certificateService.getAllCertificatesByStatusOrSearchByQuery(query, status, pageable);
    }

    @GetMapping("/{id}/views")
    public List<CertificateViewDto> getCertificateViews(@PathVariable String id) {
        return certificateViewService.getViewsByCertificate(id);
    }

    @GetMapping("/views")
    public CertificateViewPageResponse getCertificateViewsByQuery(Pageable pageable) {
        return certificateViewService.getAllViews(pageable);
    }

    @GetMapping("/views/summary")
    public List<PlatformStatsDto> getCertificateViewsSummary() {
        return certificateViewService.getTotalViewsByPlatform();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CertificateDto createCertificate(@Valid @RequestBody CertificateRequestDto certificateRequestDto) {
        return certificateService.createCertificate(certificateRequestDto);
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public CertificateDto updateCertificate(@PathVariable String id, @RequestBody CertificateDto certificateDto) {
        return certificateService.updateCertificate(id, certificateDto);
    }

    @PostMapping(path = "{id}/preview")
    public CertificatePreviewUrls addPreviewUrl(@PathVariable String id) throws IOException {
        return certificateService.addPreviewUrl(id);
    }

    @PostMapping("/{id}/publish")
    @ResponseStatus(NO_CONTENT)
    public void publishCertificate(@PathVariable String id) {
        certificateService.publishCertificate(id);
    }


    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
    }
}
