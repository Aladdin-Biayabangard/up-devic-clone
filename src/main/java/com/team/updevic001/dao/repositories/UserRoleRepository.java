package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.auth.UserRole;
import com.team.updevic001.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByName(Role role);

    @Query("""
    SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END
    FROM User u
    JOIN u.roles r
    WHERE u = :user AND r.name = :role
""")
    boolean existsByUserAndRole(User user, Role role);

}
