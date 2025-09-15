package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.payment.AdminBalance;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceMonthlyStats;
import com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminBalanceRepository extends JpaRepository<AdminBalance, Long> {

    @Query("SELECT CASE WHEN a.totalBalance >= :amount THEN true ELSE false END " +
           "FROM AdminBalance a " +
           "WHERE a.id = 1")
    Boolean hasSufficientBalance(@Param("amount") BigDecimal amount);


    @Query("""
        SELECT new com.team.updevic001.model.dtos.response.admin_dasboard.AdminBalanceStats(
            COALESCE(SUM(CASE WHEN t.status = com.team.updevic001.model.enums.PaymentStatus.PAID THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.transactionType = com.team.updevic001.model.enums.TransactionType.INCOME 
                               AND t.status = com.team.updevic001.model.enums.PaymentStatus.PAID 
                               THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.transactionType = com.team.updevic001.model.enums.TransactionType.OUTCOME 
                               AND t.status = com.team.updevic001.model.enums.PaymentStatus.PAID 
                               THEN t.amount ELSE 0 END), 0),
            null
        )
        FROM AdminPaymentTransaction t
    """)
    AdminBalanceStats getTotalStats();


    @Query(value = """
    SELECT 
        DATE_TRUNC('month', t.payment_date) AS month,
        SUM(CASE WHEN t.status='PAID' THEN t.amount ELSE 0 END) AS total,
        SUM(CASE WHEN t.transaction_type='INCOME' AND t.status='PAID' THEN t.amount ELSE 0 END) AS income,
        SUM(CASE WHEN t.transaction_type='OUTCOME' AND t.status='PAID' THEN t.amount ELSE 0 END) AS expenditure
    FROM admin_payment_transaction t
    WHERE t.payment_date >= :fromDate
    GROUP BY DATE_TRUNC('month', t.payment_date)
    ORDER BY DATE_TRUNC('month', t.payment_date)
""", nativeQuery = true)
    List<Object[]> getLastMonthsStats(@Param("fromDate") LocalDateTime fromDate);

}

