package com.team.updevic001.dao.entities;

import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import com.team.updevic001.model.dtos.certificate.PersonDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Table(name = "certificates")
public class CertificateEntity {

    @Id
    @Column(name = "credential_id", unique = true)
    String credentialId;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "firstName", column = @Column(name = "person_first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "person_last_name")),
            @AttributeOverride(name = "emailAddress", column = @Column(name = "person_email_address"))})
    PersonDto person;

    String email;

    @Column(name = "issue_date")
    LocalDate issueDate;

    @Column(name = "expire_date")
    LocalDate expireDate;

    String issuedFor;

    @Column(columnDefinition = "TEXT")
    String description;

    String previewUrlHorizontal;

    String previewUrlVertical;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "certificate_skills",
            joinColumns = @JoinColumn(name = "certificate_credential_id"))
    private List<String> skills;

    @Column(name = "issuing_organization")
    String issuingOrganization;

    @Enumerated(STRING)
    CertificateStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;

    String postMessage;
    String emailMessage;

    @Enumerated(STRING)
    CertificateType type;

}
