//package com.team.updevic001.dao.repositories;
//
//import com.team.updevic001.dao.entities.Skill;
//import com.team.updevic001.dao.entities.UserProfile;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.List;
//
//public interface SkillRepository extends CrudRepository<Skill,Long> {
//
//   @Query("SELECT s.name FROM Skill s WHERE s.userProfile=:userProfile ")
//   List<String> findSkillByUserProfile(UserProfile userProfile);
//}
