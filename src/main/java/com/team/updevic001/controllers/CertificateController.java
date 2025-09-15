package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.certificate.CertificateResponse;
import com.team.updevic001.services.impl.course.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;


    @GetMapping("/{credentialId}")
    public CertificateResponse getCertificate(@PathVariable String credentialId) {
        return certificateService.getCertificate(credentialId);
    }

    @PostMapping("/{courseId}")
    @ResponseStatus(CREATED)
    public CertificateResponse createCertificate(@PathVariable String courseId) {
        return certificateService.createCertificate(courseId);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
    }
}
