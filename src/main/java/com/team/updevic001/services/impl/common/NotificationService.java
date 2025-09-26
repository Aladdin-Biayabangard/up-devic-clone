package com.team.updevic001.services.impl.common;

import com.team.updevic001.dao.entities.EmailDraft;
import com.team.updevic001.dao.repositories.EmailDraftRepository;
import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.exceptions.ExceptionConstants;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.RecipientsGroup;
import com.team.updevic001.services.interfaces.FileLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.team.updevic001.exceptions.ExceptionConstants.EMAIL_DRAFT_NOT_FOUND;

@Async
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailServiceImpl emailServiceImpl;
    private final StudentCourseRepository studentCourseRepository;
    private final EmailDraftRepository emailDraftRepository;
    private final FileLoadService fileLoadService;

    public void saveEmailNotificationDraft(String subject,
                                           String message,
                                           Set<String> recipients,
                                           RecipientsGroup recipientsGroup,
                                           MultipartFile attachment) {
        var draft = new EmailDraft();
        draft.setSubject(subject);
        draft.setContent(message);
        draft.setRecipients(recipients);
        draft.setRecipientType(recipientsGroup);
        draft.setTemplateName("dynamic-email.html");

        if (attachment != null && !attachment.isEmpty()) {
            recipients.forEach(recipient -> saveAttachment(attachment, recipient, draft));
        }
        emailDraftRepository.save(draft);

    }

    @Async("asyncTaskExecutor")
    public void sendEmailNotificationDraft(Long draftId) {
        var draft = fetchEmailDraftIfExists(draftId);

        for (String to : draft.getRecipients()) {
            if (hasAttachment(draft)) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("customMessage", draft.getContent());
                emailServiceImpl.sendFileEmail(draft.getSubject(), to, "dynamic-email.html", variables, draft.getAttachmentPath(), null);
            }
        }
        draft.setSent(true);
        emailDraftRepository.save(draft);
    }

    public void sendEmailNotification(String subject,
                                      String message,
                                      Set<String> recipients,
                                      Set<RecipientsGroup> recipientsGroups,
                                      MultipartFile attachment) {

        emailDraftRepository.save(EmailDraft.builder()
                .subject(subject)
                .sent(true)
                .recipients(recipients)
                .content(message)
                .templateName("dynamic-email.html")
                .isRedirect(true)
                .build());
        recipients.forEach(recipient -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("customMessage", message);
            emailServiceImpl.sendFileEmail(subject, recipient, "dynamic-email.html", variables, null, attachment);
        });
    }

    public void sendNotificationForCreationNewCourse(CourseCategoryType categoryType, String courseName, String courseLink) {
        var users = studentCourseRepository.findStudentsByCourseCategoryType(categoryType);
        users.forEach(user -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("courseName", courseName);
            variables.put("courseLink", courseLink);
            emailServiceImpl.sendHtmlEmail("Added new Course", user.getEmail(), "course-created.html", variables);
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
        emailServiceImpl.sendHtmlEmail("Creation certificate", user.getEmail(), "certificate-created.html", variables);
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
        emailServiceImpl.sendHtmlEmail("Payment Successfully!", user.getEmail(), "payment-success.html", variables);

    }

    public void sendNotificationForReminder(UserEmailInfo user) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName() + " " + user.getLastName());
        emailServiceImpl.sendHtmlEmail("Reminder for login", user.getEmail(), "reminder.html", variables);
    }

    private void saveAttachment(MultipartFile file, String recipient, EmailDraft draft) {
        try {
            String fileOfWhat = "email-file";

            FileUploadResponse fileUploadResponse = fileLoadService.uploadFile(file, recipient, fileOfWhat);
            draft.setAttachmentPath(fileUploadResponse.getUrl());
            draft.setAttachmentKey(fileUploadResponse.getKey());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save attachment", e);
        }
    }

    private boolean hasAttachment(EmailDraft draft) {
        return StringUtils.hasText(draft.getAttachmentPath());
    }

    private List<String> resolveRecipients(EmailDraft draft) {
        switch (draft.getRecipientType()) {
            case ALL_USERS:
                return userRepository.findAll().stream().map(User::getEmail).toList();
            case TEACHERS:
                return groupRepository.findAllById(draft.getRecipientIds())
                        .stream()
                        .flatMap(g -> g.getUsers().stream())
                        .map(User::getEmail)
                        .distinct()
                        .toList();
            case INACTIVE_USERS:
                return groupRepository.findAllById(draft.getRecipientIds())
                        .stream()
                        .flatMap(g -> g.getUsers().stream())
                        .map(User::getEmail)
                        .distinct()
                        .toList();
            case STUDENTS:
                return userRepository.findAllById(draft.getRecipients())
                        .stream()
                        .map(User::getEmail)
                        .toList();
            default:
                return List.of();
        }
    }

    private EmailDraft fetchEmailDraftIfExists(Long draftId) {
        return emailDraftRepository.findById(draftId).orElseThrow(() -> new NotFoundException(EMAIL_DRAFT_NOT_FOUND.getCode(),
                EMAIL_DRAFT_NOT_FOUND.getMessage()));
    }
}
