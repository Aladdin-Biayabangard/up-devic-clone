package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.payment.AdminPaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminPaymentTransactionRepository extends JpaRepository<AdminPaymentTransaction, String>,
        JpaSpecificationExecutor<AdminPaymentTransaction> {
}
