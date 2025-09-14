package com.team.updevic001.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageCalculation {


    /**
     * @param amount     əsas məbləğ
     * @param percentage faiz (məsələn: 15 → 15%)
     * @return nəticə (amount * percentage / 100)
     */

    public static BigDecimal calculatePercentage(BigDecimal amount, BigDecimal percentage) {
        if (amount == null || percentage == null) {
            return BigDecimal.ZERO;
        }

        return amount
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
