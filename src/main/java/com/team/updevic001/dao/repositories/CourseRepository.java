package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.model.enums.CourseCategoryType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {

    Page<Course> findCourseByCourseCategoryType(CourseCategoryType courseCategoryType, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.courseCategoryType = :courseCategoryType")
    long countByCourseCategoryType(@Param("courseCategoryType") CourseCategoryType courseCategoryType);

    List<Course> findTop5ByOrderByRatingDesc();

    List<Course> findCourseByHeadTeacher(Teacher teacher);

    @EntityGraph(attributePaths = {"tasks","headTeacher"})
    Optional<Course> findCourseById(String id);



    @Transactional
    @Modifying
    @Query("UPDATE Course c SET c.photo_url=:photo_url,c.photoKey=:photoKey WHERE c.id=:id ")
    void updateCourseFileInfo(String id, @Param("photoKey") String fileKey, @Param("photo_url") String fileUrl);
}
