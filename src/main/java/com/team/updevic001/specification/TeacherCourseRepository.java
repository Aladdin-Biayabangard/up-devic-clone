//package com.team.updevic001.dao.repositories;
//
//import com.team.updevic001.dao.entities.Course;
//import com.team.updevic001.dao.entities.Teacher;
//import com.team.updevic001.dao.entities.TeacherCourse;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
//
//
//    @Query("SELECT tc.course.id FROM TeacherCourse tc WHERE tc.teacher = :teacher")
//    List<String> findAllCourseIdsByTeacher(@Param("teacher") Teacher teacher);
//
//
//    @Query("SELECT COUNT(tc.teacher) FROM TeacherCourse tc WHERE tc.course=:course")
//    int countTeacherByCourse(Course course);
//
////    @Query("SELECT new com.team.updevic001.model.dtos.response.teacher.TeacherNameDto" +
////            "(tc.teacher.user.firstName, tc.teacher.user.lastName) FROM TeacherCourse tc WHERE tc.course = :course")
////    List<TeacherNameDto> findTeacherNamesByCourse(@Param("course") Course course);
//
//    @Query("SELECT tc.teacher.id FROM TeacherCourse tc WHERE tc.course=:course")
//    List<Long> findTeacherIdByCourse(Course course);
//
//
//    boolean existsByCourseIdAndTeacher(String courseId, Teacher authenticatedTeacher);
//
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM TeacherCourse tc WHERE tc.course.id = :id")
//    void deleteTeacherCourseByCourseId(String id);
//}
