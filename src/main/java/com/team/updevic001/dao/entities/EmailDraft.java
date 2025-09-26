package com.team.updevic001.dao.entities;

import com.team.updevic001.model.enums.EmailStatus;
import com.team.updevic001.model.enums.RecipientsGroup;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "email_drafts")
public class EmailDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Enumerated(EnumType.STRING)
    private RecipientsGroup recipientType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<String> recipients = new HashSet<>();

    private String attachmentPath;

    private String attachmentKey;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String templateName;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    private boolean isRedirect;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime sendAt;

}
