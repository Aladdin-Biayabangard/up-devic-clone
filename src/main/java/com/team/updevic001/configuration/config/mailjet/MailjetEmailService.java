package com.team.updevic001.configuration.config.mailjet;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.team.updevic001.services.impl.common.FileLoadServiceImpl;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailjetEmailService {

    private final MailjetClient mailjetClient;
    private final TemplateEngine templateEngine;
    private final FileLoadServiceImpl fileLoadServiceImpl;

    // Logo URL (AWS S3 və ya hər hansı public URL)
    private static final String LOGO_URL = "https://updevic002.s3.eu-north-1.amazonaws.com/public/logo3.png";

    @Async("asyncTaskExecutor")
    public void sendEmail(String subject, String to, String templateName,
                          Map<String, Object> variables, String fileUrl, MultipartFile imageFile) {
        sendEmailInternal(subject, to, templateName, variables, fileUrl, imageFile);
    }

    private void sendEmailInternal(String subject, String to, String templateName,
                                   Map<String, Object> variables, String fileUrl, MultipartFile imageFile) {
        try {
            // Thymeleaf template-dən HTML body yaratmaq
            Context context = new Context();
            context.setVariables(variables);

            // HTML body (logo artıq cid ilə referans olunacaq)
            String body = templateEngine.process("email/" + templateName, context);

            JSONObject message = new JSONObject()
                    .put(Emailv31.Message.FROM, new JSONObject()
                            .put("Email", "updevic.onlinecourse@gmail.com") // Mailjet-də təsdiqlənmiş sender
                            .put("Name", "UpDevic"))
                    .put(Emailv31.Message.TO, new JSONArray()
                            .put(new JSONObject()
                                    .put("Email", to)
                                    .put("Name", to)))
                    .put(Emailv31.Message.SUBJECT, Optional.ofNullable(subject).orElse("Re-Info"))
                    .put(Emailv31.Message.HTMLPART, body);

            // Template dəyişənləri (dynamic template üçün)
            if (variables != null && !variables.isEmpty()) {
                JSONObject vars = new JSONObject(variables);
                message.put(Emailv31.Message.VARIABLES, vars);
            }

            // 🔹 Inline logo əlavə et (cid:logo üçün)
            byte[] logoBytes = fileLoadServiceImpl.downloadFileAsBytes(LOGO_URL);
            String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);

            JSONObject inlineAttachment = new JSONObject()
                    .put("ContentType", "image/png")
                    .put("Filename", "logo.png")
                    .put("ContentID", "logo.png") // template-də cid:logo ilə eyni olmalıdır
                    .put("Base64Content", logoBase64);

            message.put(Emailv31.Message.INLINEDATTACHMENTS, new JSONArray().put(inlineAttachment));

            // 🔹 File URL varsa attachment əlavə et
            if (fileUrl != null && !fileUrl.isEmpty()) {
                byte[] fileBytes = fileLoadServiceImpl.downloadFileAsBytes(fileUrl);
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

                JSONObject attachment = new JSONObject()
                        .put("ContentType", "application/octet-stream")
                        .put("Filename", fileName)
                        .put("Base64Content", Base64.getEncoder().encodeToString(fileBytes));

                message.put(Emailv31.Message.ATTACHMENTS, new JSONArray().put(attachment));
            }

            // 🔹 MultipartFile varsa attachment əlavə et
            if (imageFile != null && !imageFile.isEmpty()) {
                JSONObject attachment = new JSONObject()
                        .put("ContentType", Objects.requireNonNull(imageFile.getContentType()))
                        .put("Filename", imageFile.getOriginalFilename())
                        .put("Base64Content", Base64.getEncoder().encodeToString(imageFile.getBytes()));

                if (!message.has(Emailv31.Message.ATTACHMENTS)) {
                    message.put(Emailv31.Message.ATTACHMENTS, new JSONArray());
                }
                message.getJSONArray(Emailv31.Message.ATTACHMENTS).put(attachment);
            }

            // Mailjet request göndər
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray().put(message));

            MailjetResponse response = mailjetClient.post(request);
            System.out.println("Mailjet Status: " + response.getStatus());
            System.out.println("Mailjet Response: " + response.getData());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email via Mailjet: " + e.getMessage());
        }
    }

}
