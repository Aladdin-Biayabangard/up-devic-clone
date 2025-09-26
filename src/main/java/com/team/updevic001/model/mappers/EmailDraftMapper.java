package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.EmailDraft;
import com.team.updevic001.model.dtos.response.notification.EmailDraftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailDraftMapper {

    public EmailDraftResponse toResponse(EmailDraft draft) {
        return new EmailDraftResponse(
                draft.getId(),
                draft.getSubject(),
                draft.getRecipientType(),
                draft.getRecipients(),
                draft.getAttachmentPath(),
                draft.getAttachmentKey(),
                draft.getContent(),
                draft.getTemplateName(),
                draft.getStatus(),
                draft.isRedirect(),
                draft.getCreatedAt(),
                draft.getSendAt()
        );
    }

    public List<EmailDraftResponse> toResponse(List<EmailDraft> drafts) {
        return drafts.stream().map(this::toResponse).toList();
    }
}
