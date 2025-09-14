//package com.team.updevic001.configuration.config;
//
//
//import com.team.updevic001.dao.entities.MigrationLog;
//import com.team.updevic001.dao.repositories.MigrationLogRepository;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@Component
//@RequiredArgsConstructor
//public class StartupRunner {
//
//    private final MigrationLogRepository migrationLogRepository;
//    private final EntityManager entityManager;
//
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Async
//    @Transactional
//    public void addPaidColumnToCourses() {
//        MigrationLog log = migrationLogRepository.findById("add_paid_column_to_courses")
//                .orElse(new MigrationLog("add_paid_column_to_courses", false));
//
//        if (log.isDone()) {
//            return;
//        }
//
//        entityManager.createNativeQuery(
//                "ALTER TABLE courses ADD COLUMN IF NOT EXISTS paid BOOLEAN DEFAULT FALSE"
//        ).executeUpdate();
//
//        entityManager.createNativeQuery(
//                "UPDATE courses SET paid = FALSE WHERE paid IS NULL"
//        ).executeUpdate();
//
//        // Migration-u tamamlandı kimi işarələ
//        log.setDone(true);
//        migrationLogRepository.save(log);
//    }
//
//}
//
