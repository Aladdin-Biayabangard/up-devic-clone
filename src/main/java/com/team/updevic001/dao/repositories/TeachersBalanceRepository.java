package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.TeachersBalance;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeachersBalanceRepository extends JpaRepository<TeachersBalance, Long> {

    Optional<TeachersBalance> findByTeacher(User teacher);
}
