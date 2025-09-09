package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.CertificateEntity;
import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Task;
import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.CertificateRepository;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.certificate.CertificateResponse;
import com.team.updevic001.model.dtos.certificate.CertificatePreviewUrls;
import com.team.updevic001.model.dtos.certificate.CertificateRequestDto;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import com.team.updevic001.model.dtos.certificate.Platform;
//import com.team.updevic001.model.mappers.CertificateMapper;
import com.team.updevic001.services.interfaces.CourseService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.team.updevic001.model.enums.ExceptionConstants.CERTIFICATE_EXISTS;
import static com.team.updevic001.model.enums.ExceptionConstants.CERTIFICATE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class CertificateService {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CertificateRepository certificateRepository;
    //    private final CertificateMapper certificateMapper;
    private final CertificateViewService certificateViewService;
    private final UserService userServiceImpl;
    private final CourseService courseServiceImpl;
    private final StudentTaskRepository studentTaskRepository;
    private final TestResultRepository testResultRepository;
    private final AuthHelper authHelper;

    public CertificateResponse getCertificate(String credentialId) {
        var certificate = fetchCertificateIfExist(credentialId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        var response = new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                certificate.getIssuingOrganization(),
                certificate.getType()
        );
        return response;
    }

//    public Page<CertificateResponse> getAllCertificatesByStatusOrSearchByQuery(String query, CertificateStatus status, Pageable pageable) {
//        Page<CertificateEntity> certificates = StringUtils.isNotBlank(query)
//                ? certificateRepository.searchByNameAndStatus(query, status.name(), pageable)
//                : certificateRepository.findByStatus(status, pageable);
//
//        return certificates.map(certificateMapper::toDto);
//    }

    public CertificateResponse createMockCertificate(String courseId) {
        var credentialId = generate("ABM");
//        if (certificateRepository.existsById(credentialId)) {
//            throw new AlreadyExistsException(CERTIFICATE_EXISTS.getCode(),
//                    CERTIFICATE_EXISTS.getMessage().formatted(credentialId));
//        }
        var issuedFor = "has completed the %s course, with a score of %s";
        var user = authHelper.getAuthenticatedUser();
        var course = courseServiceImpl.findCourseById(courseId);
      //  double score = checkEligibilityForCertification(user.getId(), courseId);
        var certificate = CertificateEntity.builder()
                .credentialId(credentialId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .issueDate(LocalDate.now())
                .issuedFor(issuedFor.formatted(course.getTitle(), 90 + "%"))
                .issuingOrganization("UP-DEVIC ONLINE COURSES PLATFORM")
                .status(CertificateStatus.ACTIVE)
                .build();
        if (90 >= 91) {
            certificate.setType(CertificateType.HONOURS);
        } else {
            certificate.setType(CertificateType.NORMAL);
        }
        certificate = certificateRepository.save(certificate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        var response = new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                certificate.getIssuingOrganization(),
                certificate.getType()
        );
        return response;
    }

    public CertificateResponse createCertificate(String courseId) {
        var credentialId = generate("ABM");
        if (certificateRepository.existsById(credentialId)) {
            throw new AlreadyExistsException(CERTIFICATE_EXISTS.getCode(),
                    CERTIFICATE_EXISTS.getMessage().formatted(credentialId));
        }
        var issuedFor = "has completed the %s course, with a score of %s";
        var user = authHelper.getAuthenticatedUser();
        var course = courseServiceImpl.findCourseById(courseId);
        double score = checkEligibilityForCertification(user.getId(), courseId);
        var certificate = CertificateEntity.builder()
                .credentialId(credentialId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .issueDate(LocalDate.now())
                .issuedFor(issuedFor.formatted(course.getTitle(), score + "%"))
                .issuingOrganization("UP-DEVIC ONLINE COURSES PLATFORM")
                .status(CertificateStatus.ACTIVE)
                .build();
        if (score >= 91) {
            certificate.setType(CertificateType.HONOURS);
        } else {
            certificate.setType(CertificateType.NORMAL);
        }
        certificate = certificateRepository.save(certificate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        var response = new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                certificate.getIssuingOrganization(),
                certificate.getType()
        );
        return response;
    }

    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }

    private CertificateEntity fetchCertificateIfExist(String id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CERTIFICATE_NOT_FOUND.getCode(),
                        CERTIFICATE_NOT_FOUND.getMessage().formatted(id)));
    }

    private static String generate(String prefix) {
        String yearPart = String.format("%02d", Year.now().getValue() % 100);
        String randomPart = randomAlphanumeric();
        return String.format("%s-%s-%s",
                prefix.toUpperCase(),
                yearPart,
                randomPart
        );
    }

    private static String randomAlphanumeric() {
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            int index = RANDOM.nextInt(ALPHANUM.length());
            sb.append(ALPHANUM.charAt(index));
        }
        return sb.toString();
    }

    private double checkEligibilityForCertification(Long userId, String courseId) {
        var user = userServiceImpl.fetchUserById(userId);
        var course = courseServiceImpl.findCourseById(courseId);
        TestResult testResult = testResultRepository
                .findTestResultByStudentAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("This student is not enrolled in this course."));

        long taskCount = course.getTasks().size();
        List<Long> taskIds = course.getTasks().stream().map(Task::getId).toList();
        long countOfQuestionsAnswered = studentTaskRepository.countAllByTaskIdIn(taskIds);
        if (taskCount != countOfQuestionsAnswered) {
            throw new IllegalArgumentException("All questions must be answered to receive the certificate.");
        }

        double score = testResult.getScore();
        if (score < 60) {
            throw new IllegalArgumentException("Your score is not high enough to get a certificate.");
        }
        return score;
    }

}

//    public CertificateDto updateCertificate(String id, CertificateDto certificateDto) {
//        CertificateEntity certificate = fetchCertificateIfExist(id);
//        certificate.setPreviewUrlHorizontal(certificateDto.getPreviewUrls().getHorizontal());
//        certificate.setPreviewUrlVertical(certificateDto.getPreviewUrls().getVertical());
//        certificateMapper.updateEntityFromDto(certificateDto, certificate);
//        certificate.setCredentialId(id);
//        certificate = certificateRepository.save(certificate);
//        return certificateMapper.toDto(certificate);
//    }