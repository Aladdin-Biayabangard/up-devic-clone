package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.response.admin_dasboard.UserStatsResponse;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    long count();

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, Status status);

    @Query(value = "SELECT * FROM users u " +
                   "WHERE MATCH(u.first_name, u.last_name) AGAINST (:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    @EntityGraph(attributePaths = "roles")
    List<User> searchTeacher(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status=:status WHERE u.id=:id")
    void updateUserStatus(Long id, Status status);

    @Query("""
                SELECT new com.team.updevic001.model.dtos.response.admin_dasboard.UserStatsResponse(
                    COUNT(u), 
                                SUM(CASE WHEN u.status = 'ACTIVE' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN u.status = 'PENDING' THEN 1 ELSE 0 END)
                )
                FROM User u
            """)
    UserStatsResponse getDashboard();


    @Query("""
                SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END
                FROM User u
                JOIN u.roles r
                WHERE u = :user AND r.name = :roleName
            """)
    boolean existsByUserAndRole(@Param("user") User user, @Param("roleName") Role roleName);

    @Query("SELECT new com.team.updevic001.model.dtos.response.teacher.TeacherNameDto" +
           "(u.id, u.firstName, u.lastName, p.profilePhotoUrl) " +
           "FROM User u JOIN UserProfile p ON p.user.id = u.id " +
           "WHERE u = :user")
    TeacherNameDto findTeacherNameByUser(User user);

    @Query("SELECT CONCAT(t.firstName, ' ', t.lastName) FROM User t WHERE t.id = :id")
    String getTeacherFullName(@Param("id") Long id);

}
