package com.team.updevic001.dao.entities.payment;

import com.team.updevic001.model.enums.PaymentStatus;
import com.team.updevic001.model.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(of = "transactionId")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
@Getter
@Setter
@Entity
@Table(name = "admin_payment_transaction")
public class AdminPaymentTransaction {

    @Id
    String transactionId;

    BigDecimal amount;

    @Enumerated(STRING)
    TransactionType transactionType;

    PaymentStatus status;

    LocalDateTime paymentDate;

    String description;

    @PrePersist
    public void createTransactionId() {
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
