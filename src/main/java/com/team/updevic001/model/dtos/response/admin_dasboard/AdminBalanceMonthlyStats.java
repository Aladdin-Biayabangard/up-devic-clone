package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class AdminBalanceMonthlyStats {


    LocalDate month;
    BigDecimal monthlyTotalBalance;
    BigDecimal monthlyIncome;
    BigDecimal monthlyExpenditure;

    public AdminBalanceMonthlyStats(java.sql.Timestamp month,
                                    BigDecimal monthlyTotalBalance,
                                    BigDecimal monthlyIncome,
                                    BigDecimal monthlyExpenditure) {
        this.month = month.toLocalDateTime().toLocalDate(); // Timestamp → LocalDate
        this.monthlyTotalBalance = monthlyTotalBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpenditure = monthlyExpenditure;
    }

    public AdminBalanceMonthlyStats(java.time.LocalDateTime month,
                                    BigDecimal monthlyTotalBalance,
                                    BigDecimal monthlyIncome,
                                    BigDecimal monthlyExpenditure) {
        this.month = month.toLocalDate();
        this.monthlyTotalBalance = monthlyTotalBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpenditure = monthlyExpenditure;
    }
}
