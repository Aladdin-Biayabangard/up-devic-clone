package com.team.updevic001.configuration.mappers;


import com.team.updevic001.dao.entities.Skill;
import com.team.updevic001.dao.entities.SocialLink;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.repositories.SkillRepository;
import com.team.updevic001.dao.repositories.SocialLinkRepository;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProfileMapper {
    private final SkillRepository skillRepository;
    private final SocialLinkRepository socialLinkRepository;

//    public UserProfileDto toDto(UserProfile userProfile, List<String> skills, List<String> socialLinks) {
//        return UserProfileDto.builder()
//                .bio(userProfile.getBio())
//                .socialLinks(socialLinks)
//                .skills(skills)
//                .build();
//    }

    public UserProfile toEntity(UserProfile userProfile, UserProfileDto userProfileDto) {
        userProfile.getSocialLinks().add(createSocialLinkFromStringLinks(userProfileDto.getSocialLink(), userProfile));
        userProfile.getSkills().add(createSkillFromStringSkill(userProfileDto.getSkill(), userProfile));
        userProfile.setBio(userProfileDto.getBio());
        return userProfile;
    }

    public SocialLink createSocialLinkFromStringLinks(String socialLink, UserProfile userProfile) {
        return socialLinkRepository.save(SocialLink.builder()
                .link(socialLink)
                .userProfile(userProfile)
                .build());
    }

    public Skill createSkillFromStringSkill(String skill, UserProfile userProfile) {
        return skillRepository.save(Skill.builder()
                .name(skill)
                .userProfile(userProfile)
                .build());
    }
}
