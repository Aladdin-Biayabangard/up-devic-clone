package com.team.updevic001.services.impl.notification;

import com.team.updevic001.dao.entities.EmailDraft;
import com.team.updevic001.dao.repositories.EmailDraftRepository;
import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.model.dtos.request.EmailDraftRequest;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.utility.RecipientResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.team.updevic001.model.enums.EmailStatus.SEND;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailServiceImpl emailServiceImpl;
    private final StudentCourseRepository studentCourseRepository;
    private final EmailDraftRepository emailDraftRepository;


    private final RecipientResolver recipientResolver;
    private final EmailDraftService draftService;
    private final EmailDraftService emailDraftService;

    public void sendEmailDraft(Long draftId) {
        var draft = draftService.fetcEmailDraft(draftId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("customMessage", draft.getContent());

        Set<String> recipients = draft.getRecipients() != null && !draft.getRecipients().isEmpty()
                ? draft.getRecipients()
                : recipientResolver.resolveRecipients(draft.getRecipientType());

        for (String email : recipients) {
            emailServiceImpl.sendFileEmail(
                    draft.getSubject(),
                    email,
                    draft.getTemplateName(),
                    variables,
                    draft.getAttachmentPath(),
                    null
            );
        }
        draft.setStatus(SEND);
    }


    public void sendEmailNotificationDirect(EmailDraftRequest request,
                                            MultipartFile attachment) {
        var draft = EmailDraft.builder()
                .subject(request.getSubject())
                .status(SEND)
                .recipients(request.getRecipients())
                .recipientType(request.getRecipientsGroup())
                .content(request.getMessage())
                .templateName("dynamic-email.html")
                .isRedirect(true)
                .build();

        emailDraftService.saveAttachment(attachment, draft);

        emailDraftRepository.save(draft);

        Map<String, Object> variables = new HashMap<>();
        variables.put("customMessage", request.getMessage());

        Set<String> emails = draft.getRecipients() != null && !draft.getRecipients().isEmpty()
                ? draft.getRecipients()
                : recipientResolver.resolveRecipients(request.getRecipientsGroup());

        for (String email : emails) {
            emailServiceImpl.sendFileEmail(
                    draft.getSubject(),
                    email,
                    draft.getTemplateName(),
                    variables,
                    draft.getAttachmentPath(),
                    null
            );
        }
    }


    public void sendNotificationForCreationNewCourse(CourseCategoryType categoryType, String courseName, String
            courseLink) {
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

}
