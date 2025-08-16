package com.team.updevic001.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface CertificateService {

    ResponseEntity<Resource> generateCertificate(String courseId) throws IOException;

    double checkEligibilityForCertification(Long userId, String courseId);
}
