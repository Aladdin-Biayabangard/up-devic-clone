package com.team.updevic001.dao.entities.payment;

import com.team.updevic001.model.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Builder
@Getter
@Setter
@Entity
@Table(name = "teacher_payments_transaction")
public class TeacherPaymentTransaction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String transactionId;

    private Long teacherId;

    private String teacherName;

    private String teacherEmail;

    private String courseId;

    private BigDecimal amount;

    @Enumerated(STRING)
    private PaymentStatus status;

    private String description;

    private LocalDateTime paymentDateAndTime;

    @PrePersist
    public void createTransactionId() {
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
    }
}
