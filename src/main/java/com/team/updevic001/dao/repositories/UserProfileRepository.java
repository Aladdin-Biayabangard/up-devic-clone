package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

//    @EntityGraph(attributePaths = {"socialLinks", "skills"})
    UserProfile findByUser(User user);

    @Query("SELECT up FROM UserProfile up WHERE up.user IN :users")
    List<UserProfile> findByUsers(@Param("users") List<User> users);


    @Transactional
    @Modifying
    @Query("UPDATE UserProfile p SET p.profilePhoto_url=:photo_url , p.profilePhotoKey=:photoKey WHERE p.user.id=:id ")
    void updateCourseFileInfo(Long id, @Param("photoKey")String fileKey, @Param("photo_url") String fileUrl);

    @Query("SELECT up.profilePhotoKey FROM UserProfile up WHERE up.user=:user")
    Optional<String> findProfilePhotoKeyBy(User user);
}
