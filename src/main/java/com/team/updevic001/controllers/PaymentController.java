package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.PaymentRequest;
import com.team.updevic001.model.dtos.response.payment.StripeResponse;
import com.team.updevic001.services.interfaces.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentServiceImpl;


    @Operation(summary = "Kursa qeydiyyat ucun odenis edilir")
    @GetMapping(path = "success/{courseId}")
    public ResponseEntity<String> success(@PathVariable String courseId) {
        paymentServiceImpl.paymentStatus(courseId);
        return ResponseEntity.ok("Payment successfully!");
    }


    @GetMapping(path = "cancel/{courseId}")
    public ResponseEntity<String> cancel(@PathVariable String courseId) {
        return ResponseEntity.ok("Payment failed. Please try again.");
    }

    @Operation(summary = "Kursu almaq üçün ")
    @PostMapping
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody PaymentRequest request) {
        StripeResponse stripeResponse = paymentServiceImpl.checkoutProducts(request);
        return ResponseEntity.ok(stripeResponse);
    }

    @Operation(summary = "Muellimin balansini gosterir")
    @GetMapping(path = "balance")
    public ResponseEntity<BigDecimal> teacherBalance() {
        BigDecimal teacherBalance = paymentServiceImpl.teacherBalance();
        return ResponseEntity.ok(teacherBalance);
    }
}
