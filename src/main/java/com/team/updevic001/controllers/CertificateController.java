package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.certificate.CertificateResponse;
import com.team.updevic001.model.dtos.certificate.CertificateRequestDto;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateViewDto;
import com.team.updevic001.model.dtos.certificate.CertificateViewPageResponse;
import com.team.updevic001.model.dtos.certificate.Platform;
import com.team.updevic001.model.dtos.certificate.PlatformStatsDto;
import com.team.updevic001.services.impl.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
//    private final CertificateViewService certificateViewService;


    @GetMapping("/{credentialId}")
    public CertificateResponse getCertificate(@PathVariable String credentialId) {
        return certificateService.getCertificate(credentialId);
    }
//
//    @GetMapping("/search")
//    public Page<CertificateResponse> getAllCertificatesOrSearchByQuery(@RequestParam(required = false) String query,
//                                                                       @RequestParam CertificateStatus status,
//                                                                       Pageable pageable) {
//        return certificateService.getAllCertificatesByStatusOrSearchByQuery(query, status, pageable);
//    }

//    @GetMapping("/{id}/views")
//    public List<CertificateViewDto> getCertificateViews(@PathVariable String id) {
//        return certificateViewService.getViewsByCertificate(id);
//    }
//
//    @GetMapping("/views")
//    public CertificateViewPageResponse getCertificateViewsByQuery(Pageable pageable) {
//        return certificateViewService.getAllViews(pageable);
//    }
//
//    @GetMapping("/views/summary")
//    public List<PlatformStatsDto> getCertificateViewsSummary() {
//        return certificateViewService.getTotalViewsByPlatform();
//    }

    @PostMapping("/{courseId}")
    @ResponseStatus(CREATED)
    public CertificateResponse createCertificate(@PathVariable String courseId) {
        return certificateService.createCertificate(courseId);
    }

    @PostMapping("mock/{courseId}")
    @ResponseStatus(CREATED)
    public CertificateResponse createMockCertificate(@PathVariable String courseId) {
        return certificateService.createMockCertificate(courseId);
    }

//    @PutMapping("{id}")
//    @ResponseStatus(NO_CONTENT)
//    public CertificateResponse updateCertificate(@PathVariable String id, @RequestBody CertificateResponse certificateResponse) {
//        return certificateService.updateCertificate(id, certificateResponse);
//    }

    /// /
//    @PostMapping(path = "{id}/preview")
//    public CertificatePreviewUrls addPreviewUrl(@PathVariable String id) throws IOException {
//        return certificateService.addPreviewUrl(id);
//    }

//    @PostMapping("/{id}/publish")
//    @ResponseStatus(NO_CONTENT)
//    public void publishCertificate(@PathVariable String id) {
//        certificateService.publishCertificate(id);
//    }
    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
    }
}
