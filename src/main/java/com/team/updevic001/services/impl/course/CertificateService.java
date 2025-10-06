package com.team.updevic001.services.impl.course;

import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.course.CertificateEntity;
import com.team.updevic001.dao.entities.course.Course;
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
import java.time.format.DateTimeFormatter;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import static com.team.updevic001.exceptions.ExceptionConstants.CERTIFICATE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CERTIFICATE_LINK = "https://updevic.lovable.app/certificates/";
    private static final String ISSUED_FOR_TEMPLATE = "has completed the %s course, with a score of %s";

    private final CertificateRepository certificateRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final StudentTaskRepository studentTaskRepository;
    private final TestResultRepository testResultRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuthHelper authHelper;

    public CertificateResponse getCertificate(String credentialId) {
        CertificateEntity certificate = fetchCertificateIfExist(credentialId);
        return mapToResponse(certificate);
    }

    public CustomPage<CertificateResponseForAdmin> getAllCertificates(CertificateCriteria criteria, CustomPageRequest request) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Specification<CertificateEntity> filter = null;
        if (criteria != null &&
            (criteria.getEmail() != null || criteria.getTrainingName() != null || criteria.getStatus() != null ||
             criteria.getType() != null || criteria.getDateFrom() != null || criteria.getToDate() != null)) {
            filter = CertificateSpecification.filter(criteria);
        }

        var certificates = (filter == null) ? certificateRepository.findAll(pageable)
                : certificateRepository.findAll(filter, pageable);

        return new CustomPage<>(
                certificates.getContent().stream().map(this::mapToAdminResponse).toList(),
                certificates.getNumber(),
                certificates.getSize()
        );
    }

    @Transactional
    public CertificateResponse createCertificate(String courseId) {
        User user = authHelper.getAuthenticatedUser();
        Course course = courseService.findCourseById(courseId);

        CertificateEntity certificate = certificateRepository
                .findCertificateEntityByCourseIdAndUserId(courseId, user.getId())
                .orElseGet(() -> buildAndSaveCertificate(course, user));

        sendCertificateNotification(certificate, user, course);

        return mapToResponse(certificate);
    }

    public void deleteCertificate(String credentialId) {
        certificateRepository.deleteById(credentialId);
    }

    public int checkEligibilityForCertification(Long userId, String courseId) {
        User user = userService.fetchUserById(userId);
        Course course = courseService.findCourseById(courseId);

        validateAllTasksCompleted(courseId);
        TestResult testResult = testResultRepository.findTestResultByStudentAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("This student is not enrolled in this course."));

        validateScore(testResult.getScore());
        return (int) Math.round(testResult.getScore());
    }


    private CertificateEntity fetchCertificateIfExist(String id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CERTIFICATE_NOT_FOUND.getCode(),
                        CERTIFICATE_NOT_FOUND.getMessage().formatted(id)));
    }

    private CertificateEntity buildAndSaveCertificate(Course course, User user) {
        double score = taskRepository.countByCourseId(course.getId()) > 0
                ? checkEligibilityForCertification(user.getId(), course.getId())
                : 100;
        String credentialId = generateCredentialId();
        CertificateEntity certificate = CertificateEntity.builder()
                .credentialId(credentialId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .issueDate(LocalDate.now())
                .issuedFor(String.format(ISSUED_FOR_TEMPLATE, course.getTitle(), score + "%"))
                .trainingName(course.getTitle())
                .teacherName(userRepository.getTeacherFullName(course.getTeacher().getId()))
                .issuingOrganization("UP-DEVIC ONLINE COURSES PLATFORM")
                .status(CertificateStatus.ACTIVE)
                .userId(user.getId())
                .courseId(course.getId())
                .tags(course.getTags())
                .description(course.getDescription())
                .certificateUrl(CERTIFICATE_LINK + credentialId)
                .type(score >= 91 ? CertificateType.HONOURS : CertificateType.NORMAL)
                .build();

        return certificateRepository.save(certificate);
    }

    private void sendCertificateNotification(CertificateEntity certificate, User user, Course course) {
        notificationService.sendNotificationForCreationCertificate(
                new UserEmailInfo(user.getFirstName(), user.getLastName(), user.getEmail()),
                course.getTitle(),
                certificateScore(certificate),
                CERTIFICATE_LINK + certificate.getCredentialId()
        );
    }

    private CertificateResponse mapToResponse(CertificateEntity certificate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d',' yyyy", Locale.ENGLISH);
        String formattedDate = certificate.getIssueDate().format(formatter);
        return new CertificateResponse(
                certificate.getCredentialId(),
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

    private CertificateResponseForAdmin mapToAdminResponse(CertificateEntity certificate) {
        return new CertificateResponseForAdmin(
                certificate.getFirstName() + " " + certificate.getLastName(),
                certificate.getTrainingName(),
                certificate.getTeacherName(),
                certificate.getCreatedAt(),
                certificate.getIssueDate(),
                certificate.getCertificateUrl(),
                null
        );
    }

    private void validateAllTasksCompleted(String courseId) {
        long taskCount = taskRepository.countByCourseId(courseId);
        List<Long> taskIds = taskRepository.findIdsByCourseId(courseId);
        long answeredCount = studentTaskRepository.countAllByTaskIdIn(taskIds);
        if (taskCount != answeredCount) {
            throw new IllegalArgumentException("All questions must be answered to receive the certificate.");
        }
    }

    private void validateScore(double score) {
        if (score < 60) {
            throw new IllegalArgumentException("Your score is not high enough to get a certificate.");
        }
    }

    private double certificateScore(CertificateEntity certificate) {
        try {
            String scoreStr = certificate.getIssuedFor().split(", with a score of ")[1].replace("%", "");
            return Double.parseDouble(scoreStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private String generateCredentialId() {
        String yearPart = String.format("%02d", Year.now().getValue() % 100);
        return "ABM-" + yearPart + "-" + randomAlphanumeric();
    }

    private String randomAlphanumeric() {
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}
