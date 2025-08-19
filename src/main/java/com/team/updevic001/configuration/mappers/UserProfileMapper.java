package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserProfileMapper {


    public UserProfile toEntity(UserProfile userProfile, UserProfileDto userProfileDto) {
        if (!userProfileDto.getSocialLink().isEmpty()) {
            userProfile.setSocialLinks(userProfileDto.getSocialLink());
        }
        if (!userProfileDto.getSkill().isEmpty()) {
            userProfile.setSkills(userProfileDto.getSkill());
        }
        if (!userProfileDto.getBio().isEmpty()) {
            userProfile.setBio(userProfileDto.getBio());
        }
        return userProfile;
    }


}
