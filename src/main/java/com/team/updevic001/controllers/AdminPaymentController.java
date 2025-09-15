package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceStats;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminPaymentTransactionResponse;
import com.team.updevic001.services.impl.payment.AdminBalanceService;
import com.team.updevic001.services.impl.payment.AdminTransactionService;
import com.team.updevic001.specification.criteria.AdminTransactionCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminTransactionService adminTransactionService;
    private final AdminBalanceService adminBalanceService;

    @GetMapping
    public CustomPage<AdminPaymentTransactionResponse> getAdminTransactions(
            CustomPageRequest request,
            AdminTransactionCriteria criteria) {
        return adminTransactionService.getTransactions(request, criteria);
    }

    @GetMapping(path = "stats")
    public AdminBalanceStats adminBalanceStats() {
        return adminBalanceService.adminBalanceStats();
    }

    @PutMapping("/{transactionId}")
    public void updateTeacherPaymentTransaction(
            @PathVariable String transactionId,
            @RequestParam String description) {
        adminTransactionService.updateDescription(transactionId, description);
    }
}
