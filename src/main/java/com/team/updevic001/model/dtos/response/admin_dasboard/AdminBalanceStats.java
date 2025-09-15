package com.team.updevic001.model.dtos.response.admin_dasboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminBalanceStats {

    BigDecimal totalBalance;
    BigDecimal totalIncome;
    BigDecimal totalExpenditure;
    List<AdminBalanceMonthlyStats> monthlyStats;
}
