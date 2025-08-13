package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query(value = "SELECT * FROM teachers t " +
            "JOIN users u ON t.student_id = u.id " +
            "WHERE MATCH(u.first_name, u.last_name) AGAINST (:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    List<Teacher> searchTeacher(@Param("keyword") String keyword);

    List<Teacher> findTeacherByBalanceGreaterThanEqual(BigDecimal balance);

    Optional<Teacher> findByUserId(Long userId);

    boolean existsTeacherByUserId(Long id);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE teachers AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
