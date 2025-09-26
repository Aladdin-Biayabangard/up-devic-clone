package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.EmailDraft;
import com.team.updevic001.model.enums.EmailStatus;
import org.springframework.data.jpa.domain.Specification;

public class EmailDraftSpecification {

    public static Specification<EmailDraft> hasSubject(String subject) {
        return (root, query, criteriaBuilder) ->
                subject == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), "%" + subject.toLowerCase() + "%");
    }

    public static Specification<EmailDraft> hasStatus(EmailStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<EmailDraft> filter(String subject, EmailStatus status) {
        return Specification.where(hasSubject(subject))
                .and(hasStatus(status));
    }
}
