package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.model.dtos.notification.UserEmailInfo;
import com.team.updevic001.model.dtos.response.admin_dasboard.UserStatsResponse;
import com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo;
import com.team.updevic001.model.dtos.response.teacher.TeacherNameDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
            SELECT new com.team.updevic001.model.dtos.response.teacher.TeacherMainInfo( 
                        t.id,  CONCAT(t.firstName,' ', t.lastName ),t.email) 
                        FROM User t  WHERE t =:user 
            """)
    TeacherMainInfo getTeacherMainInfoById(User user);

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

    @Query("""
            SELECT DISTINCT new com.team.updevic001.model.dtos.notification.UserEmailInfo(
                                        u.firstName,
                                        u.lastName,
                                        u.email) 
                                        FROM User u WHERE u.lastLogin <= :oneMonthAgo
            
            """)
    List<UserEmailInfo> findUsersInactiveSince(@Param("oneMonthAgo") LocalDateTime oneMonthAgo);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM User u 
        WHERE u.status = 'PENDING' 
          AND u.createdAt <= :oneMonthAgo
        """)
    int deletePendingUsersSince(@Param("oneMonthAgo") LocalDateTime oneMonthAgo);
}
