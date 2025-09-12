package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    boolean existsByUserIdAndBrowserAndDeviceAndOs(Long userId, String browser, String device, String os);

    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId);

    @Modifying
    @Query("UPDATE LoginHistory l SET l.currentSession = false WHERE l.user.id = :userId AND l.currentSession = true")
    void updateCurrentSessionFalse(@Param("userId") Long userId);
}