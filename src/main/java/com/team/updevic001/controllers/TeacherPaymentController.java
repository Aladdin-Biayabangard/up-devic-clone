package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.payment.TeacherPaymentResponse;
import com.team.updevic001.services.impl.payment.TeachersPaymentTransactionService;
import com.team.updevic001.specification.criteria.TeacherPaymentCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admins/teacher-payments")
@RequiredArgsConstructor
public class TeacherPaymentController {

    private final TeachersPaymentTransactionService teachersPaymentTransactionService;

    @GetMapping
    public CustomPage<TeacherPaymentResponse> getTeacherPayments(
            CustomPageRequest request,
            TeacherPaymentCriteria criteria) {
        return teachersPaymentTransactionService.getTeacherPayments(request, criteria);
    }


    @PostMapping("/{id}/pay")
    public void toPayTheTeacher(@PathVariable Long id) {
        teachersPaymentTransactionService.toPayTheTeacher(id);
    }

    /**
     * Mövcud transaction-a description əlavə edir / dəyişir.
     */
    @PutMapping("/{id}")
    public void updateTeacherPaymentTransaction(
            @PathVariable("id") Long paymentTeacherId,
            @RequestParam String description) {
        teachersPaymentTransactionService.updateTeacherPaymentTransaction(paymentTeacherId, description);
    }
}
