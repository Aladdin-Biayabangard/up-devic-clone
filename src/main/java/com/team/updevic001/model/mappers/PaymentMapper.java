package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.TeacherPaymentTransaction;
import com.team.updevic001.model.dtos.response.payment.TeacherPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    public TeacherPaymentResponse toResponse(TeacherPaymentTransaction payment) {
        return new TeacherPaymentResponse(
                payment.getId(),
                payment.getTeacherId(),
                payment.getTeacherName(),
                payment.getTeacherEmail(),
                payment.getCourseId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getDescription(),
                payment.getPaymentDateAndTime()

        );
    }

    public List<TeacherPaymentResponse> toResponse(List<TeacherPaymentTransaction> payments) {
        return payments.stream().map(this::toResponse).toList();
    }
}
