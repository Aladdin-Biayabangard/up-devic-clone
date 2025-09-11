package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.CertificateEntity;
import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.repositories.CertificateRepository;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.certificate.CertificateResponse;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import com.team.updevic001.services.interfaces.CourseService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.team.updevic001.model.enums.ExceptionConstants.CERTIFICATE_EXISTS;
import static com.team.updevic001.model.enums.ExceptionConstants.CERTIFICATE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class CertificateService {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CertificateRepository certificateRepository;
    private final UserService userServiceImpl;
    private final CourseService courseServiceImpl;
    private final StudentTaskRepository studentTaskRepository;
    private final TestResultRepository testResultRepository;
    private final AuthHelper authHelper;
    private final TaskRepository taskRepository;

    public CertificateResponse getCertificate(String credentialId) {
        var certificate = fetchCertificateIfExist(credentialId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        var response = new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                certificate.getTrainingName(),
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



    public CertificateResponse createCertificate(String courseId) {
        var issuedFor = "has completed the %s course, with a score of %s";
        var user = authHelper.getAuthenticatedUser();
        var course = courseServiceImpl.findCourseById(courseId);
        Optional<String> optionalCertificate = certificateRepository.findCredentialIdByUserIdAndCourseId(user.getId(), courseId);
        if (optionalCertificate.isPresent()) {
            throw new AlreadyExistsException(CERTIFICATE_EXISTS.getCode(),
                    CERTIFICATE_EXISTS.getMessage().formatted(optionalCertificate.get()));
        }
        var credentialId = generate("ABM");
        double score = checkEligibilityForCertification(user.getId(), courseId);
        var certificate = CertificateEntity.builder()
                .credentialId(credentialId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .issueDate(LocalDate.now())
                .issuedFor(issuedFor.formatted(course.getTitle(), score + "%"))
                .trainingName(course.getTitle())
                .issuingOrganization("UP-DEVIC ONLINE COURSES PLATFORM")
                .status(CertificateStatus.ACTIVE)
                .userId(user.getId())
                .courseId(courseId)
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
                course.getTitle(),
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
    public int checkEligibilityForCertification(Long userId, String courseId) {
        var user = userServiceImpl.fetchUserById(userId);
        var course = courseServiceImpl.findCourseById(courseId);
        TestResult testResult = testResultRepository
                .findTestResultByStudentAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("This student is not enrolled in this course."));

        long taskCount = taskRepository.countByCourseId(courseId);
        List<Long> taskIds = taskRepository.findIdsByCourseId(courseId);
        long countOfQuestionsAnswered = studentTaskRepository.countAllByTaskIdIn(taskIds);
        if (taskCount != countOfQuestionsAnswered) {
            throw new IllegalArgumentException("All questions must be answered to receive the certificate.");
        }

        double score = testResult.getScore();
        if (score < 60) {
            throw new IllegalArgumentException("Your score is not high enough to get a certificate.");
        }

        return (int) Math.round(score);
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