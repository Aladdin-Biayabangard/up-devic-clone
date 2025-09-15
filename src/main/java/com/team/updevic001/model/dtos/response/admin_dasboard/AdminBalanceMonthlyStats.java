package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminBalanceMonthlyStats {

    LocalDate month;
    BigDecimal monthlyTotalBalance;
    BigDecimal monthlyIncome;
    BigDecimal monthlyExpenditure;
}
