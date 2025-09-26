package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.EmailDraftRequest;
import com.team.updevic001.services.impl.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/draft/send/{id}")
    public void sendDraft(@PathVariable("id") Long draftId) {
        notificationService.sendEmailDraft(draftId);
    }


    @PostMapping(path = "/send/direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void sendDirect(
            @ModelAttribute("emailDraftRequest") EmailDraftRequest emailDraftRequest,
            @RequestPart(value = "file", required = false) final MultipartFile attachment) {

        notificationService.sendEmailNotificationDirect(emailDraftRequest, attachment);
    }
}
