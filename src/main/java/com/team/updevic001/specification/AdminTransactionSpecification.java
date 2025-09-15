package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.payment.AdminPaymentTransaction;
import com.team.updevic001.model.enums.TransactionType;
import com.team.updevic001.specification.criteria.AdminTransactionCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AdminTransactionSpecification {

    public static Specification<AdminPaymentTransaction> hasTransactionType(TransactionType transactionType) {
        return (root, query, cb) ->
                transactionType == null ? null :
                        cb.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<AdminPaymentTransaction> fromDate(LocalDate fromDate) {
        return (root, query, cb) ->
                fromDate == null ? null :
                        cb.greaterThanOrEqualTo(root.get("paymentDate"), fromDate.atStartOfDay());
    }

    public static Specification<AdminPaymentTransaction> toDate(LocalDate toDate) {
        return (root, query, cb) ->
                toDate == null ? null :
                        cb.lessThanOrEqualTo(root.get("paymentDate"), toDate.atTime(23, 59, 59));
    }

    public static Specification<AdminPaymentTransaction> filter(AdminTransactionCriteria criteria) {
        return Specification.where(hasTransactionType(criteria.getTransactionType()))
                .and(fromDate(criteria.getDateFrom()))
                .and(toDate(criteria.getToDate()));
    }
}
