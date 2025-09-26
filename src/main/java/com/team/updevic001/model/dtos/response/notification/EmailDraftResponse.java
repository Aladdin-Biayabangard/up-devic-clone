package com.team.updevic001.model.dtos.response.notification;

import com.team.updevic001.model.enums.EmailStatus;
import com.team.updevic001.model.enums.RecipientsGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDraftResponse {

    private Long id;

    private String subject;

    private RecipientsGroup recipientType;

    private Set<String> recipients;

    private String attachmentPath;

    private String attachmentKey;

    private String content;

    private String templateName;

    private EmailStatus status;

    private boolean isRedirect;

    private LocalDateTime createdAt;

    private LocalDateTime sendAt;
}
