package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.CourseRating;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    List<CourseRating> findByCourse(Course course);

    Optional<CourseRating> findCourseRatingByCourseAndUser(Course course, User user);

    void deleteAllByCourseId(String courseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CourseRating cr WHERE cr.course.id =:id")
    void deleteRatingByCourseId(String id);

}
