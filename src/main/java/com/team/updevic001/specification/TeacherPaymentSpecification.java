package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.TeacherPaymentTransaction;
import com.team.updevic001.model.enums.PaymentStatus;
import com.team.updevic001.specification.criteria.TeacherPaymentCriteria;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDate;

public class TeacherPaymentSpecification {

    public static Specification<TeacherPaymentTransaction> hasEmail(String email) {
        return (root, query, cb) ->
                email == null ? null :
                        cb.like(cb.lower(root.get("teacherEmail")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<TeacherPaymentTransaction> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<TeacherPaymentTransaction> fromDate(LocalDate fromDate) {
        return (root, query, cb) ->
                fromDate == null ? null :
                        cb.greaterThanOrEqualTo(root.get("paymentDateAndTime"), fromDate.atStartOfDay());
    }

    public static Specification<TeacherPaymentTransaction> toDate(LocalDate toDate) {
        return (root, query, cb) ->
                toDate == null ? null :
                        cb.lessThanOrEqualTo(root.get("paymentDateAndTime"), toDate.atTime(23, 59, 59));
    }

    public static Specification<TeacherPaymentTransaction> filter(TeacherPaymentCriteria criteria) {
        return Specification.where(hasEmail(criteria.getEmail()))
                .and(hasStatus(criteria.getStatus()))
                .and(fromDate(criteria.getFromDate()))
                .and(toDate(criteria.getToDate()));
    }
}


