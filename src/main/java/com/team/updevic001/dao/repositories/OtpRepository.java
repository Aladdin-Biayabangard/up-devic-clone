package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    Optional<Otp> findByCodeAndEmailAndUsedFalse(Integer otpCode,String email);
}
