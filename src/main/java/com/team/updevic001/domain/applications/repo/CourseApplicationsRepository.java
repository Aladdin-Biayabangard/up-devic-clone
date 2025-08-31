package com.team.updevic001.domain.applications.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CourseApplicationsRepository extends JpaRepository<CourseApplicationsEntity, UUID>,
        JpaSpecificationExecutor<CourseApplicationsEntity> {

    Optional<CourseApplicationsEntity> findByEmail(String email);

    Page<CourseApplicationsEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
