package com.team.updevic001.services.impl.notification;

import com.team.updevic001.dao.entities.EmailDraft;
import com.team.updevic001.dao.repositories.EmailDraftRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.request.EmailDraftRequest;
import com.team.updevic001.model.dtos.response.notification.EmailDraftResponse;
import com.team.updevic001.model.enums.EmailStatus;
import com.team.updevic001.model.mappers.EmailDraftMapper;
import com.team.updevic001.services.interfaces.FileLoadService;
import com.team.updevic001.specification.EmailDraftSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.team.updevic001.exceptions.ExceptionConstants.EMAIL_DRAFT_NOT_FOUND;
import static com.team.updevic001.model.enums.EmailStatus.PENDING;

@Service
@RequiredArgsConstructor
public class EmailDraftService {

    private final EmailDraftRepository emailDraftRepository;
    private final FileLoadService fileLoadService;
    private final EmailDraftMapper emailDraftMapper;

    public void saveEmailDraft(EmailDraftRequest request,
                               MultipartFile attachment) {

        var draft = EmailDraft.builder()
                .subject(request.getSubject())
                .content(request.getMessage())
                .recipients(request.getRecipients())
                .recipientType(request.getRecipientsGroup())
                .status(PENDING)
                .templateName("dynamic-email.html")
                .build();

        if (attachment != null && !attachment.isEmpty()) {
            saveAttachment(attachment, draft);
        }

        emailDraftRepository.save(draft);

    }

    public EmailDraftResponse getEmailDraft(Long draftId) {
        var emailDraft = fetcEmailDraft(draftId);
        return emailDraftMapper.toResponse(emailDraft);
    }

    public CustomPage<EmailDraftResponse> getDrafts(CustomPageRequest request, String subject, EmailStatus status) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);
        Specification<EmailDraft> filter = null;
        if ((subject != null && !subject.isEmpty()) ||
                status != null) {
            filter = EmailDraftSpecification.filter(subject, status);
        }

        var allDrafts = (filter == null)
                ? emailDraftRepository.findAll(pageable)
                : emailDraftRepository.findAll(filter, pageable);

        return new CustomPage<>(
                emailDraftMapper.toResponse(allDrafts.getContent()),
                allDrafts.getNumber(),
                allDrafts.getSize()
        );
    }

    public EmailDraft fetcEmailDraft(Long draftId) {
        return emailDraftRepository.findById(draftId)
                .orElseThrow(() -> new NotFoundException(
                        EMAIL_DRAFT_NOT_FOUND.getCode(),
                        EMAIL_DRAFT_NOT_FOUND.getMessage()));
    }

    public void saveAttachment(MultipartFile file, EmailDraft draft) {
        try {
            if (file != null) {
                var response = fileLoadService.uploadFile(file, "email", ":email-file");
                draft.setAttachmentPath(response.getUrl());
                draft.setAttachmentKey(response.getKey());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save attachment", e);
        }
    }
}
