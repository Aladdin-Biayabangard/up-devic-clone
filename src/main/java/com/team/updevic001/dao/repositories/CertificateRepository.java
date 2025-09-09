package com.team.updevic001.dao.repositories;


import com.team.updevic001.dao.entities.CertificateEntity;
import com.team.updevic001.model.dtos.certificate.CertificateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CertificateRepository extends JpaRepository<CertificateEntity, String> {

    @Query(value = """
            SELECT * FROM certificates c
            WHERE (LOWER(c.person_first_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(c.person_last_name) LIKE LOWER(CONCAT('%', :query, '%')))
                        AND c.status = :status
            ORDER BY c.person_first_name ASC
            """,
            nativeQuery = true)
    Page<CertificateEntity> searchByNameAndStatus(@Param("query") String query,
                                                  @Param("status") String status,
                                                  Pageable pageable);

    Page<CertificateEntity> findByStatus(CertificateStatus status, Pageable pageable);

    @Query("SELECT c.credentialId FROM CertificateEntity c WHERE c.userId = :userId AND c.courseId = :courseId")
    Optional<String> findCredentialIdByUserIdAndCourseId(@Param("userId") Long userId,
                                                         @Param("courseId") String courseId);

}
