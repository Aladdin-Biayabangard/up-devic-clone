//package com.team.updevic001.dao.repositories;
//
//
//import com.team.updevic001.dao.entities.CertificateViewEntity;
//import com.team.updevic001.model.dtos.certificate.Platform;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface CertificateViewRepository extends JpaRepository<CertificateViewEntity, String> {
//
//    List<CertificateViewEntity> findAllByCertificateId(String certificateId);
//
//    Optional<CertificateViewEntity> findByCertificateIdAndPlatform(String certificateId, Platform platform);
//
//    @Query("SELECT cv.platform, SUM(cv.viewCount) FROM CertificateViewEntity cv GROUP BY cv.platform")
//    List<Object[]> getTotalViewsByPlatform();
//}
