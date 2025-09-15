package com.team.updevic001.services.impl.payment;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminPaymentTransactionResponse;
import com.team.updevic001.model.mappers.AdminTransactionMapper;
import com.team.updevic001.specification.AdminTransactionSpecification;
import com.team.updevic001.specification.criteria.AdminTransactionCriteria;
import com.team.updevic001.dao.entities.payment.AdminPaymentTransaction;
import com.team.updevic001.dao.entities.payment.TeachersBalance;
import com.team.updevic001.dao.repositories.AdminPaymentTransactionRepository;
import com.team.updevic001.dao.repositories.TeachersBalanceRepository;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.enums.PaymentStatus;
import com.team.updevic001.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.team.updevic001.exceptions.ExceptionConstants.ADMIN_TRANSACTION_NOT_FOUND;
import static com.team.updevic001.model.enums.TransactionType.INCOME;
import static com.team.updevic001.model.enums.TransactionType.OUTCOME;

@Service
@RequiredArgsConstructor
public class AdminTransactionService {

    private final AdminPaymentTransactionRepository adminPaymentTransactionRepository;
    private final AdminBalanceService adminBalanceService;
    private final TeacherBalanceService teacherBalanceService;
    private final TeachersBalanceRepository teachersBalanceRepository;
    private final AdminTransactionMapper adminTransactionMapper;

    public CustomPage<AdminPaymentTransactionResponse> getTransactions(CustomPageRequest request, AdminTransactionCriteria criteria) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);
        Specification<AdminPaymentTransaction> filter = null;
        if (criteria.getTransactionType() != null ||
            criteria.getDateFrom() != null ||
            criteria.getToDate() != null) {
            filter = AdminTransactionSpecification.filter(criteria);
        }

        var adminPayments = (filter == null)
                ? adminPaymentTransactionRepository.findAll(pageable)
                : adminPaymentTransactionRepository.findAll(filter, pageable);
        return new CustomPage<>(
                adminTransactionMapper.toResponse(adminPayments.getContent()),
                adminPayments.getNumber(),
                adminPayments.getSize()
        );
    }

    public void updateDescription(String transactionId, String description) {
        var paymentTransaction = fetchAdminTransaction(transactionId);
        paymentTransaction.setDescription(description);
        adminPaymentTransactionRepository.save(paymentTransaction);
    }

    @Transactional
    public void payTeachersFee(Long teacherId, BigDecimal amount) {
        if (adminBalanceService.hasSufficientBalance(amount)) {
            TeachersBalance teachersBalance = teacherBalanceService.fetchTeacherBalanceIfExists(teacherId);
            teachersBalance.setBalance(teachersBalance.getBalance().add(amount));
            teachersBalanceRepository.save(teachersBalance);
            adminBalanceService.calculateExpenditure(amount);
            createTransaction(OUTCOME, amount, "teacher: " + teacherId);
        }
    }

    public void balanceIncrease(BigDecimal amount) {
        adminBalanceService.calculateIncome(amount);
        createTransaction(INCOME, amount, null);
    }

    public void createTransaction(TransactionType type, BigDecimal amount, String description) {
        adminPaymentTransactionRepository.save(AdminPaymentTransaction.builder()
                .transactionType(type)
                .description(description)
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PAID)
                .amount(amount)
                .build());
    }

    public AdminPaymentTransaction fetchAdminTransaction(String transactionId) {
        return adminPaymentTransactionRepository.findById(transactionId).orElseThrow(() ->
                new NotFoundException(ADMIN_TRANSACTION_NOT_FOUND.getCode(),
                        ADMIN_TRANSACTION_NOT_FOUND.getMessage().formatted(transactionId)
                ));
    }
}
