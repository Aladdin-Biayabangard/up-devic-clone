package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserProfileMapper {


    public UserProfile toEntity(UserProfile userProfile, UserProfileDto userProfileDto) {
        userProfile.setSocialLinks(userProfileDto.getSocialLink());
        userProfile.setSkills(userProfile.getSocialLinks());
        userProfile.setBio(userProfileDto.getBio());
        return userProfile;
    }


}
