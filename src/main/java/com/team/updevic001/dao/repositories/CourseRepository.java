package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.model.enums.CourseCategoryType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    @Query(value = "SELECT * FROM courses WHERE document_with_weights  @@ plainto_tsquery(:keyword) ORDER BY ts_rank(document_with_weights, plainto_tsquery(:keyword)) DESC LIMIT 50",
            nativeQuery = true)
    List<Course> searchCoursesByKeyword(@Param("keyword") String keyword);


    List<Course> findByIdGreaterThanOrderByIdAscRatingDescCreatedAtDesc(Long id, Pageable pageable);

    List<Course> findCourseByCourseCategoryType(CourseCategoryType courseCategoryType);

    List<Course> findTop5ByOrderByRatingDesc();

    List<Course> findCourseByHeadTeacher(Teacher teacher);

    @Query("SELECT c.photoKey FROM Course c WHERE c.id=:id ")
    Optional<String> findProfilePhotoKeyBy(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Course c SET c.photo_url=:photo_url,c.photoKey=:photoKey WHERE c.id=:id ")
    void updateCourseFileInfo(Long id,@Param("photoKey")String fileKey,@Param("photo_url") String fileUrl);
}
