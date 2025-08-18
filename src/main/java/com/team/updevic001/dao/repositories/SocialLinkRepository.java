//package com.team.updevic001.dao.repositories;
//
//import com.team.updevic001.dao.entities.SocialLink;
//import com.team.updevic001.dao.entities.UserProfile;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface SocialLinkRepository extends CrudRepository<SocialLink,Long> {
//
//    @Query("SELECT s.link FROM SocialLink s WHERE s.userProfile=:userProfile")
//    List<String> findSocialLinkNameByUserProfile(UserProfile userProfile);
//
//    @Query("SELECT s.link FROM SocialLink s WHERE s.userProfile IN :userProfiles")
//    List<SocialLink> findByUserProfiles(@Param("userProfiles") List<UserProfile> userProfiles);
//
//}
