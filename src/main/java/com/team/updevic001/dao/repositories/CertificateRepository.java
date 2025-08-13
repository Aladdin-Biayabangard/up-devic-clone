package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Certificate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CertificateRepository extends CrudRepository<Certificate,Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Certificate c WHERE c.course.id = :id")
    void deleteCertificateByCourseId(Long id);
}
