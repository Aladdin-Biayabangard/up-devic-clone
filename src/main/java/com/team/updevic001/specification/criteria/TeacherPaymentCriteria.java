package com.team.updevic001.specification.criteria;

import com.team.updevic001.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherPaymentCriteria {
    private String email;
    private PaymentStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;

}
