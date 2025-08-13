package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.model.projection.UserView;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);


    List<User> findByFirstNameContainingIgnoreCase(String query);

    long count();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<UserView> findUsersByRole(Role role);


    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, Status status);


    List<UserView> findByIdGreaterThanOrderByIdAsc(Long afterId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status=:status WHERE u.id=:id")
    void updateUserStatus(Long id, Status status);

    @Query("""
    SELECT COUNT(u) FROM User u
    WHERE NOT EXISTS (
        SELECT 1 FROM u.roles r WHERE r.name = 'ADMIN'
    )
""")
    int countNonAdminUsers();



}
