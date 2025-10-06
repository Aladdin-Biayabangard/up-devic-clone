package com.team.updevic001.services.impl.course;

import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.entities.course.CertificateEntity;
import com.team.updevic001.dao.repositories.CertificateRepository;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.certificate.CertificateResponse;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.admin_dasboard.CertificateResponseForAdmin;
import com.team.updevic001.services.impl.notification.NotificationService;
import com.team.updevic001.services.interfaces.CourseService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.specification.CertificateSpecification;
import com.team.updevic001.specification.criteria.CertificateCriteria;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.team.updevic001.exceptions.ExceptionConstants.CERTIFICATE_NOT_FOUND;


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
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final static String CERTIFICATE_LINK = "https://up-devic-001.onrender.com/api/v1/certificates/";

    public CertificateResponse getCertificate(String credentialId) {
        var certificate = fetchCertificateIfExist(credentialId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        return new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                certificate.getTrainingName(),
                certificate.getTeacherName(),
                certificate.getIssuingOrganization(),
                certificate.getType(),
                certificate.getDescription(),
                certificate.getTags()
        );
    }

    public CustomPage<CertificateResponseForAdmin> getAllCertificates(CertificateCriteria criteria, CustomPageRequest request) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Specification<CertificateEntity> filter = null;

        if (criteria.getEmail() != null ||
            criteria.getTrainingName() != null ||
            criteria.getStatus() != null ||
            criteria.getType() != null ||
            criteria.getDateFrom() != null ||
            criteria.getToDate() != null) {

            filter = CertificateSpecification.filter(criteria);
        }

        var certificates = (filter == null)
                ? certificateRepository.findAll(pageable)
                : certificateRepository.findAll(filter, pageable);

        return new CustomPage<>(
                certificates.getContent().stream().map(certificateEntity -> new CertificateResponseForAdmin(
                        certificateEntity.getFirstName() + " " + certificateEntity.getLastName(),
                        certificateEntity.getTrainingName(),
                        certificateEntity.getTeacherName(),
                        certificateEntity.getCreatedAt(),
                        certificateEntity.getIssueDate(),
                        null,
                        null

                )).toList(),
                certificates.getNumber(),
                certificates.getSize());

    }

    @Transactional
    public CertificateResponse createCertificate(String courseId) {
        var issuedFor = "has completed the %s course, with a score of %s";
        var formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        var user = authHelper.getAuthenticatedUser();
        var course = courseServiceImpl.findCourseById(courseId);

        var optionalCertificate = certificateRepository.findCertificateEntityByCourseIdAndUserId(courseId, user.getId());
        if (optionalCertificate.isPresent()) {
            CertificateEntity certificate = optionalCertificate.get();
            String formattedDate = certificate.getIssueDate().format(formatter);
            return new CertificateResponse(certificate.getCredentialId(),
                    certificate.getFirstName() + " " + certificate.getLastName(),
                    formattedDate,
                    certificate.getIssuedFor(),
                    course.getTitle(),
                    certificate.getTeacherName(),
                    certificate.getIssuingOrganization(),
                    certificate.getType(),
                    certificate.getDescription(),
                    certificate.getTags());

        }
        var credentialId = generate();
        double score;
        if (taskRepository.countByCourseId(courseId) > 0) {
            score = checkEligibilityForCertification(user.getId(), courseId);
        } else {
            score = 100;
        }
        var certificate = CertificateEntity.builder()
                .credentialId(credentialId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .issueDate(LocalDate.now())
                .issuedFor(issuedFor.formatted(course.getTitle(), score + "%"))
                .trainingName(course.getTitle())
                .teacherName(userRepository.getTeacherFullName(course.getTeacher().getId()))
                .issuingOrganization("UP-DEVIC ONLINE COURSES PLATFORM")
                .status(CertificateStatus.ACTIVE)
                .userId(user.getId())
                .courseId(courseId)
                .tags(course.getTags())
                .description(course.getDescription())
                .build();
        if (score >= 91) {
            certificate.setType(CertificateType.HONOURS);
        } else {
            certificate.setType(CertificateType.NORMAL);
        }
        certificate = certificateRepository.save(certificate);
        String formattedDate = certificate.getIssueDate().format(formatter);
        notificationService.sendNotificationForCreationCertificate(
                new UserEmailInfo(user.getFirstName(), user.getLastName(), user.getEmail()),
                course.getTitle(),
                score,
                CERTIFICATE_LINK + credentialId);
        return new CertificateResponse(certificate.getCredentialId(),
                certificate.getFirstName() + " " + certificate.getLastName(),
                formattedDate,
                certificate.getIssuedFor(),
                course.getTitle(),
                certificate.getTeacherName(),
                certificate.getIssuingOrganization(),
                certificate.getType(),
                certificate.getDescription(),
                certificate.getTags()
        );
    }

    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }

    private static String generate() {
        String yearPart = String.format("%02d", Year.now().getValue() % 100);
        String randomPart = randomAlphanumeric();
        return String.format("%s-%s-%s",
                "ABM",
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

    private CertificateEntity fetchCertificateIfExist(String id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CERTIFICATE_NOT_FOUND.getCode(),
                        CERTIFICATE_NOT_FOUND.getMessage().formatted(id)));
    }


}
