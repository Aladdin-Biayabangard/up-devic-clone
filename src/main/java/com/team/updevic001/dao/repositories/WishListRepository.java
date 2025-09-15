package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.course.Course;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.course.WishList;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WishListRepository extends CrudRepository<WishList, Long> {

    @Query("SELECT wl.course FROM WishList wl WHERE wl.user=:user")
    Page<Course> findCourseByUser(User user, Pageable pageable);

    @Transactional
    @Modifying
    void deleteWishListByCourseIdAndUser(String courseId, User user);

    boolean existsWishListByCourseAndUser(Course course, User authenticatedUser);
}
