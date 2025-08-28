package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

    public ResponseTeacherDto toTeacherDto(User user, UserProfile userProfile) {
        return new ResponseTeacherDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                userProfile.getSpeciality(),
                userProfile.getExperienceYears(),
                userProfile.getBio(),
                userProfile.getSocialLinks(),
                userProfile.getSkills(),
                userProfile.getHireDate(),
                userProfile.getProfilePhotoUrl()
        );
    }
}
