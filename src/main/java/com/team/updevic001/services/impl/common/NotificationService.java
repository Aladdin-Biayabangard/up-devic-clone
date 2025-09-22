package com.team.updevic001.services.impl.common;

import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.model.enums.CourseCategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Async
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailServiceImpl emailServiceImpl;
    private final StudentCourseRepository studentCourseRepository;

    public void sendNotificationForCreationNewCourse(CourseCategoryType categoryType, String courseName, String courseLink) {
        var users = studentCourseRepository.findStudentsByCourseCategoryType(categoryType);
        users.forEach(user -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("courseName", courseName);
            variables.put("courseLink", courseLink);
            emailServiceImpl.sendHtmlEmail(user.getEmail(), "course-created.html", variables);
        });
    }

    public void sendNotificationForCreationCertificate(UserEmailInfo user,
                                                       String courseName,
                                                       double score,
                                                       String certificateLink) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName() + " " + user.getLastName());
        variables.put("courseName", courseName);
        variables.put("score", score);
        variables.put("certificateLink", certificateLink);
        emailServiceImpl.sendHtmlEmail(user.getEmail(), "certificate-created.html", variables);
    }

    public void sendNotificationForSuccessfullyPayment(UserEmailInfo user,
                                                       String courseName,
                                                       double amount,
                                                       String courseLink) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName() + " " + user.getLastName());
        variables.put("courseName", courseName);
        variables.put("amountPaid", amount);
        variables.put("courseLink", courseLink);
        emailServiceImpl.sendHtmlEmail(user.getEmail(), "payment-success.html", variables);

    }

    public void sendNotificationForReminder(UserEmailInfo user) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName() + " " + user.getLastName());
        emailServiceImpl.sendHtmlEmail(user.getEmail(), "reminder.html", variables);
    }
}
