package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.TeacherApplicationsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TeacherApplicationsRepository extends JpaRepository<TeacherApplicationsEntity, UUID>,
        JpaSpecificationExecutor<TeacherApplicationsEntity> {

    Page<TeacherApplicationsEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(t) FROM TeacherApplicationsEntity t WHERE t.status = 'PENDING'")
    Long countPendingApplications();
}
