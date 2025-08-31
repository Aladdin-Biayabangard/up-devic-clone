package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.MigrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationLogRepository extends JpaRepository<MigrationLog, String> {}
