package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.WishList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WishListRepository extends CrudRepository<WishList, Long> {

    @Query("SELECT wl.course FROM WishList wl WHERE wl.user=:user")
    List<Course> findCourseByUser(User user);

    @Transactional
    @Modifying
    void deleteWishListByCourseIdAndUser(Long courseId, User user);

    boolean existsWishListByCourseAndUser(Course course, User authenticatedUser);
}
