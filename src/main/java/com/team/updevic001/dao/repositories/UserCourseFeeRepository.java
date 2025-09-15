package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.course.Course;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.payment.UserCourseFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface UserCourseFeeRepository extends JpaRepository<UserCourseFee, Long> {

    boolean existsUserCourseFeeByCourseAndUser(Course course, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserCourseFee fe WHERE fe.course.id = :id")
    void deleteCourseFeeByCourseId(String id);
}
