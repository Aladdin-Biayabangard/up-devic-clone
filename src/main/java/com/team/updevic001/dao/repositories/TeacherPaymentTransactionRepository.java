package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.TeacherPaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeacherPaymentTransactionRepository extends JpaRepository<TeacherPaymentTransaction, Long>,
        JpaSpecificationExecutor<TeacherPaymentTransaction> {
}
