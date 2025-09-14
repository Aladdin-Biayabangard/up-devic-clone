package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;


public interface PaymentService {
    StripeResponse checkoutProducts(PaymentRequest request);

    void paymentSuccess(String courseId);

}
