package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.StudentCourse;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    @Query("SELECT uc.student FROM StudentCourse uc WHERE uc.course.id = :courseId")
    List<User> findUsersByCourse(@Param("courseId") String courseId);

    @Query("SELECT c FROM Course c JOIN StudentCourse sc ON sc.course = c WHERE sc.student = :student")
    List<Course> findCourseByStudent(@Param("student") User student);

    boolean existsByStudentAndCourse(User student, Course course);

    @Query("SELECT COUNT(DISTINCT uc.student.id) FROM StudentCourse uc WHERE uc.course.id IN :courseIds")
    int countAllStudentsByCourseIds(@Param("courseIds") List<String> courseIds);

    boolean existsByCourseAndStudent(Course course, User student);

    void deleteStudentCourseByCourseAndStudent(Course course, User student);

    @Query("SELECT count (sc.student) from StudentCourse sc WHERE sc.course=:course")
    int countStudentByCourse(Course course);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentCourse sc WHERE sc.course.id = :id")
    void deleteStudentCourseByCourseId(String id);
}

