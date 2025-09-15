package com.team.updevic001.specification;

import com.team.updevic001.specification.criteria.CertificateCriteria;
import com.team.updevic001.dao.entities.course.CertificateEntity;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import com.team.updevic001.model.dtos.certificate.CertificateType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class CertificateSpecification {

    public static Specification<CertificateEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<CertificateEntity> hasTrainingName(String trainingName) {
        return (root, query, criteriaBuilder) ->
                trainingName == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("trainingName")), "%" + trainingName.toLowerCase() + "%");
    }

    public static Specification<CertificateEntity> hasStatus(CertificateStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<CertificateEntity> hasType(CertificateType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null :
                        criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<CertificateEntity> issuedAfter(LocalDate fromDate) {
        return (root, query, criteriaBuilder) ->
                fromDate == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("issueDate"), fromDate);
    }

    public static Specification<CertificateEntity> issuedBefore(LocalDate toDate) {
        return (root, query, criteriaBuilder) ->
                toDate == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(root.get("issueDate"), toDate);
    }

    public static Specification<CertificateEntity> filter(CertificateCriteria criteria) {
        return Specification.where(hasEmail(criteria.getEmail()))
                .and(hasTrainingName(criteria.getTrainingName()))
                .and(hasStatus(criteria.getStatus()))
                .and(hasType(criteria.getType()))
                .and(issuedAfter(criteria.getDateFrom()))
                .and(issuedBefore(criteria.getToDate()));
    }
}
