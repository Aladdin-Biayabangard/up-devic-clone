package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.CertificateEntity;
import com.team.updevic001.dao.entities.ScreenshotConfigEntity;
import com.team.updevic001.dao.repositories.CertificateRepository;
import com.team.updevic001.dao.repositories.ScreenshotConfigRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.certificate.CertificateDto;
import com.team.updevic001.model.dtos.certificate.CertificatePreviewUrls;
import com.team.updevic001.model.dtos.certificate.CertificateRequestDto;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.Platform;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.mappers.CertificateMapper;
import com.team.updevic001.services.interfaces.FileLoadService;
import com.team.updevic001.utility.screenshot.ByteArrayMultipartFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

import static com.team.updevic001.model.dtos.certificate.CertificateStatus.DRAFT;
import static com.team.updevic001.model.enums.ExceptionConstants.CERTIFICATE_NOT_FOUND;
import static com.team.updevic001.model.enums.Status.ACTIVE;
import static com.team.updevic001.model.enums.Status.INACTIVE;


@Service
@RequiredArgsConstructor
public class CertificateService {

    private final String EMAIL_NOTIFICATIONS_TOPIC = "email-notifications";

    private final CertificateRepository certificateRepository;
    private final CertificateMapper certificateMapper;
    private final CertificateViewService certificateViewService;
    private final ScreenshotConfigRepository screenshotConfigRepository;
//    private final ScreenshotService screenshotService;
    private final FileLoadService fileLoadService;

    public CertificateDto getCertificate(String id, Platform platform, Authentication authentication) {
        var certificate = fetchCertificateIfExist(id);
        certificateViewService.incrementViewCount(id, platform);
        CertificateDto dto = certificateMapper.toDto(certificate);
        CertificatePreviewUrls certificatePreviewUrls = CertificatePreviewUrls.builder()
                .vertical(certificate.getPreviewUrlVertical())
                .horizontal(certificate.getPreviewUrlHorizontal())
                .build();
        dto.setPreviewUrls(certificatePreviewUrls);
        if (!isUserAuthorized(authentication)) {
            dto.setEmail("");
            return dto;
        }
        return dto;
    }

    public Page<CertificateDto> getAllCertificatesByStatusOrSearchByQuery(String query, CertificateStatus status, Pageable pageable) {
        Page<CertificateEntity> certificates = StringUtils.isNotBlank(query)
                ? certificateRepository.searchByNameAndStatus(query, status.name(), pageable)
                : certificateRepository.findByStatus(status, pageable);

        return certificates.map(certificateMapper::toDto);
    }


    public CertificateDto createCertificate(CertificateRequestDto certificateRequestDto) {
        CertificateEntity certificate = CertificateEntity.builder()
                .person(certificateRequestDto.getPerson())
                .email(certificateRequestDto.getEmail())
                .credentialId(CredentialIdGenerator.generate("ING"))
                .description(certificateRequestDto.getDescription())
                .issuedFor(certificateRequestDto.getIssuedFor())
                .skills(certificateRequestDto.getSkills())
                .expireDate(certificateRequestDto.getExpireDate())
                .issueDate(certificateRequestDto.getIssueDate())
                .issuingOrganization(certificateRequestDto.getIssuingOrganization())
                .status(certificateRequestDto.getStatus())
                .postMessage(certificateRequestDto.getPostMessage())
                .emailMessage(certificateRequestDto.getEmailMessage())
                .type(certificateRequestDto.getType())
                .build();

        certificate = certificateRepository.save(certificate);
        return certificateMapper.toDto(certificate);
    }

    public void publishCertificate(String id) {
        var certificate = fetchCertificateIfExist(id);
//
//        if (certificate.getStatus() == DRAFT || certificate.getStatus() == INACTIVE) {
//            certificate.setStatus(ACTIVE);
//        }
//
//        kafkaTemplate.send(EMAIL_NOTIFICATIONS_TOPIC, certificate.getCredentialId(), createEmail(certificate.getCredentialId()));
    }


    public CertificateDto updateCertificate(String id, CertificateDto certificateDto) {
        CertificateEntity certificate = fetchCertificateIfExist(id);
        certificate.setPreviewUrlHorizontal(certificateDto.getPreviewUrls().getHorizontal());
        certificate.setPreviewUrlVertical(certificateDto.getPreviewUrls().getVertical());
        certificateMapper.updateEntityFromDto(certificateDto, certificate);
        certificate.setCredentialId(id);
        certificate = certificateRepository.save(certificate);
        return certificateMapper.toDto(certificate);
    }

//    public CertificatePreviewUrls addPreviewUrl(String id) throws IOException {
//        CertificateEntity certificate = fetchCertificateIfExist(id);
//        ScreenshotConfigEntity screenshotConfigEntityH = screenshotConfigRepository.findById("cert-screen-horizontal").orElseThrow();
//        String hUrl = generateCertificateUrl(screenshotConfigEntityH.getUrl(), id);
//        screenshotConfigEntityH.setUrl(hUrl);
//        String horizontalPreviewUrl = takeScreenshot(screenshotConfigEntityH, id);
//        certificate.setPreviewUrlHorizontal(horizontalPreviewUrl);
//        certificateRepository.save(certificate);
//
//        ScreenshotConfigEntity screenshotConfigEntityV = screenshotConfigRepository.findById("cert-screen-vertical").orElseThrow();
//        String vUrl = generateCertificateUrl(screenshotConfigEntityV.getUrl(), id);
//        screenshotConfigEntityV.setUrl(vUrl);
//        String verticalPreviewUrl = takeScreenshot(screenshotConfigEntityV, id);
//        certificate.setPreviewUrlVertical(verticalPreviewUrl);
//        certificateRepository.save(certificate);
//        return CertificatePreviewUrls.builder()
//                .horizontal(horizontalPreviewUrl)
//                .vertical(verticalPreviewUrl)
//                .build();
//    }


    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }

    private CertificateEntity fetchCertificateIfExist(String id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CERTIFICATE_NOT_FOUND.getCode(),
                        CERTIFICATE_NOT_FOUND.getMessage().formatted(id)));
    }

    private static class CredentialIdGenerator {
        private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final SecureRandom RANDOM = new SecureRandom();

        public static String generate(String prefix) {
            String yearPart = String.format("%02d", Year.now().getValue() % 100);
            String randomPart = randomAlphanumeric(5);
            return String.format("%s-%s-%s",
                    prefix.toUpperCase(),
                    yearPart,
                    randomPart
            );
        }

        private static String randomAlphanumeric(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(ALPHANUM.length());
                sb.append(ALPHANUM.charAt(index));
            }
            return sb.toString();
        }
    }


    private boolean isUserAuthorized(Authentication authentication) {
        if (authentication == null)
            return false;
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        return userPrincipal.getAuthorities().stream().anyMatch(a ->
//                ROLE_ADMIN.name().equals(a.getAuthority())
//                || ROLE_STAFF.name().equals(a.getAuthority()));
        return true;
    }
//
//    private String takeScreenshot(ScreenshotConfigEntity screenshotConfigEntity, String id) throws IOException {
////        byte[] captured = screenshotService.capture(screenshotConfigEntity);
//        MultipartFile multipartFile = new ByteArrayMultipartFile(captured, id + ".png", id + ".png", MediaType.IMAGE_PNG.getType());
//        if (multipartFile != null) {
//            FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(multipartFile, id, "");
//            return fileUploadResponse.getUrl();
//        }
//        return null;
//    }

    public String generateCertificateUrl(String url, String id) {
        return url.replace("{id}", id);
    }
}
