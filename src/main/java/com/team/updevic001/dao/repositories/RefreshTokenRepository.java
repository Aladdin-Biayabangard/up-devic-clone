package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByIdAndExpiresAtAfter(Long id, LocalDateTime now);
}