package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.payment.TeachersBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeachersBalanceRepository extends JpaRepository<TeachersBalance, Long> {

    Optional<TeachersBalance> findByTeacherId(Long teacherId);
}
