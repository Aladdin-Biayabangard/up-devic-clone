package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserProfileDto;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.projection.UserView;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public ResponseUserProfileDto toUserProfileDto(String firstName, String lastName, UserProfile userProfile, List<String> roles) {
        return new ResponseUserProfileDto(
                firstName,
                lastName,
                userProfile.getProfilePhoto_url(),
                userProfile.getBio(),
                userProfile.getSocialLinks(),
                userProfile.getSkills(),
                roles
        );
    }

    public <E, D> D toResponse(E entity, Class<D> dtoClass) {
        D dto = modelMapper.map(entity, dtoClass);

        if (dto instanceof ResponseTeacherDto responseTeacherDto) {
            responseTeacherDto.setHireDate(LocalDateTime.now());
        }

        return dto;
    }

    public UserResponseForAdmin toResponseForAdmin(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name()) // Enum -> String
                .toList();
        return new UserResponseForAdmin(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roleNames,
                user.getStatus()
        );
    }

    public List<UserResponseForAdmin> toResponseForAdmin(List<User> users) {
        return users.stream().map(this::toResponseForAdmin).toList();
    }


    public ResponseUserDto toResponseFromUser(User user) {
        return new ResponseUserDto(user.getFirstName(), user.getLastName(), user.getEmail());
    }


}
