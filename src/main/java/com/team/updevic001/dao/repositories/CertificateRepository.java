package com.team.updevic001.dao.repositories;


import com.team.updevic001.dao.entities.course.CertificateEntity;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;


public interface CertificateRepository extends JpaRepository<CertificateEntity, String>, JpaSpecificationExecutor<CertificateEntity> {

    Optional<CertificateEntity> findCertificateEntityByCourseIdAndUserId(String courseId, Long userId);

    long countCertificateEntityByStatus(CertificateStatus status);


}
