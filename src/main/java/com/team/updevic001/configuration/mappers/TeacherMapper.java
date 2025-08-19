package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

    public ResponseTeacherDto toTeacherDto(Teacher teacher, UserProfile userProfile) {
        return new ResponseTeacherDto(
                teacher.getUser().getFirstName(),
                teacher.getUser().getLastName(),
                teacher.getUser().getEmail(),
                teacher.getSpeciality(),
                teacher.getExperienceYears(),
                userProfile.getBio(),
                userProfile.getSocialLinks(),
                userProfile.getSkills(),
                teacher.getHireDate()
        );
    }
}
