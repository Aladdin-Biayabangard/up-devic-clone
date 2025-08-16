package com.team.updevic001.controllers;

import com.team.updevic001.services.interfaces.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateServiceImpl;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadCertificate(@RequestParam String courseId) throws IOException {
        return certificateServiceImpl.generateCertificate(courseId);
    }
}
