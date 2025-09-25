package com.team.updevic001.mail;

import com.team.updevic001.services.interfaces.FileLoadService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl {

    JavaMailSender mailSender;
    TemplateEngine templateEngine;
    ClassPathResource logo = new ClassPathResource("static/logo.png");
    private final FileLoadService fileLoadService;

    @Async("asyncTaskExecutor")
    public void sendHtmlEmail(String subject, String to, String templateName, Map<String, Object> variables) {
        sendEmailInternal(subject, to, templateName, variables, null,null);
    }

    @Async("asyncTaskExecutor")
    public void sendFileEmail(String subject, String to, String templateName, Map<String, Object> variables, String fileUrl,MultipartFile imageFile) {
        sendEmailInternal(subject, to, templateName, variables, fileUrl,imageFile);
    }


    private void sendEmailInternal(String subject, String to, String templateName,
                                   Map<String, Object> variables, String fileUrl,MultipartFile imageFile) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String body = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(Optional.ofNullable(subject).orElse("Re-Info"));
            helper.setText(body, true);

            if (fileUrl != null && !fileUrl.isEmpty()) {
                byte[] fileBytes = fileLoadService.downloadFileAsBytes(fileUrl);
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                helper.addAttachment(fileName, new ByteArrayResource(fileBytes));
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                helper.addAttachment(
                        Objects.requireNonNull(imageFile.getOriginalFilename()),
                        new ByteArrayResource(imageFile.getBytes())
                );
            }

            helper.addInline("logo", logo);

            mailSender.send(message);
            log.info("HTML email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }


}