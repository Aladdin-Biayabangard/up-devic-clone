package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;

import java.math.BigDecimal;

public interface PaymentService {
    StripeResponse checkoutProducts(PaymentRequest request);

    void paymentStatus(Long courseId);

    BigDecimal teacherBalance();


}
