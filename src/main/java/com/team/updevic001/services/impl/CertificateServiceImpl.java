package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.StudentTaskRepository;
import com.team.updevic001.dao.repositories.TestResultRepository;
import com.team.updevic001.model.enums.CertificateTemplate;
import com.team.updevic001.services.interfaces.CertificateService;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CourseServiceImpl courseServiceImpl;
    private final TestResultRepository testResultRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final AuthHelper authHelper;
    private final UserServiceImpl userServiceImpl;


    @Override
    public ResponseEntity<Resource> generateCertificate(Long courseId) throws IOException {
        User user = authHelper.getAuthenticatedUser();
        Course course = courseServiceImpl.findCourseById(courseId);
        double score = checkEligibilityForCertification(user.getId(), courseId);

        DecimalFormat df = new DecimalFormat("#.0");
        String testScore = df.format(score);


        ByteArrayOutputStream pdfOutput;
        if (score >= 91) {
            pdfOutput = CertificateTemplate.RED.generateCertificate(user.getFirstName(), course.getTitle(), testScore);
        } else if (score >= 75) {
            pdfOutput = CertificateTemplate.BLUE.generateCertificate(user.getFirstName(), course.getTitle(), testScore);
        } else {
            pdfOutput = CertificateTemplate.SIMPLE.generateCertificate(user.getFirstName(), course.getTitle(), testScore);
        }

        ByteArrayResource resource = new ByteArrayResource(pdfOutput.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + user.getFirstName() + "_certificate.pdf\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @Override
    public double checkEligibilityForCertification(Long userId, Long courseId) {
        User user = userServiceImpl.fetchUserById(userId);
        Course course = courseServiceImpl.findCourseById(courseId);
        long taskCount = course.getTasks().size();
        List<Long> taskIds = course.getTasks().stream().map(Task::getId).toList();

        long countOfQuestionsAnswered = studentTaskRepository.countAllByTaskIdIn(taskIds);
        if (taskCount != countOfQuestionsAnswered) {
            throw new IllegalArgumentException("All questions must be answered to receive the certificate.");
        }

        TestResult testResult = testResultRepository
                .findTestResultByStudentAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("This student is not enrolled in this course."));
        double score = testResult.getScore();
        if (score < 60) {
            throw new IllegalArgumentException("Your score is not high enough to get a certificate.");
        }
        return score;
    }

}
