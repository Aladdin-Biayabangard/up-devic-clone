package com.team.updevic001.model.dtos.response.admin_dasboard;

import com.team.updevic001.model.enums.PaymentStatus;
import com.team.updevic001.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminPaymentTransactionResponse {

    private String transactionId;

    private BigDecimal amount;

    private TransactionType transactionType;

    private PaymentStatus status;

    private LocalDateTime paymentDate;

    private String description;
}
