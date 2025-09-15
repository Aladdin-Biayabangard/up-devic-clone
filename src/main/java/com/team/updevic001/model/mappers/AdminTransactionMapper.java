package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.payment.AdminPaymentTransaction;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminPaymentTransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class AdminTransactionMapper {
    public AdminPaymentTransactionResponse toResponse(AdminPaymentTransaction transaction) {
        return new AdminPaymentTransactionResponse(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getPaymentDate(),
                transaction.getDescription()
        );
    }

    public List<AdminPaymentTransactionResponse> toResponse(List<AdminPaymentTransaction> transactions) {
        return transactions.stream().map(this::toResponse).toList();
    }
}
