package com.team.updevic001.model.dtos.response.payment;

import com.team.updevic001.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherPaymentResponse {

    private Long id;

    private Long teacherId;

    private String teacherName;

    private String teacherEmail;

    private String courseId;

    private BigDecimal amount;

    private PaymentStatus status;

    private String description;

    private LocalDateTime paymentDateAndTime;

}
