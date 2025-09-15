package com.team.updevic001.services.impl.payment;

import com.team.updevic001.dao.entities.payment.AdminBalance;
import com.team.updevic001.dao.repositories.AdminBalanceRepository;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceMonthlyStats;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBalanceService {

    private final AdminBalanceRepository adminBalanceRepository;

    public AdminBalanceStats adminBalanceStats() {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(6);
        var totalStats = adminBalanceRepository.getTotalStats();
        List<AdminBalanceMonthlyStats> stats = adminBalanceRepository.getLastMonthsStats(fromDate).stream()
                .map(row -> new AdminBalanceMonthlyStats(
                        ((Timestamp) row[0]).toLocalDateTime().toLocalDate(),
                        (BigDecimal) row[1],
                        (BigDecimal) row[2],
                        (BigDecimal) row[3]
                ))
                .toList();
        totalStats.setMonthlyStats(stats);
        return totalStats;
    }

    public void calculateIncome(BigDecimal amount) {
        AdminBalance adminBalance = fetchAdminBalance();
        adminBalance.setIncome(adminBalance.getIncome().add(amount));
        adminBalance.setTotalBalance(adminBalance.getTotalBalance().add(adminBalance.getIncome()));
        adminBalanceRepository.save(adminBalance);
    }

    public void calculateExpenditure(BigDecimal amount) {
        AdminBalance adminBalance = fetchAdminBalance();
        adminBalance.setExpenditure(adminBalance.getExpenditure().subtract(amount));
        adminBalance.setTotalBalance(adminBalance.getTotalBalance().subtract(adminBalance.getExpenditure()));
        adminBalanceRepository.save(adminBalance);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return adminBalanceRepository.hasSufficientBalance(amount);
    }

    public AdminBalance fetchAdminBalance() {
        return adminBalanceRepository.findById(1L).orElseGet(
                () -> adminBalanceRepository.save(AdminBalance.builder()
                        .totalBalance(BigDecimal.valueOf(18000))
                        .income(BigDecimal.valueOf(5000))
                        .expenditure(BigDecimal.valueOf(3000))
                        .build()));
    }

}
