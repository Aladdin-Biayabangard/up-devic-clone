package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import com.team.updevic001.model.dtos.request.security.ChangePasswordDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserProfileDto;
import com.team.updevic001.model.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    ResponseUserDto getUserById(Long id);

    ResponseUserProfileDto getUserProfile();

    void updateUserProfileInfo(UserProfileDto userProfileDto);

    void uploadUserPhoto(MultipartFile multipartFile) throws IOException;

    void updateUserPassword(ChangePasswordDto passwordDto);

    boolean existsByUserAndRole( User user,  Role roleName);

    User fetchUserById(Long id);

}
