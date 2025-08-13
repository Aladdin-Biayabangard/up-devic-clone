package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import com.team.updevic001.model.dtos.request.security.ChangePasswordDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    ResponseUserDto getUserById(Long id);

    List<ResponseUserDto> getUser(String query);

    ResponseUserProfileDto getUserProfile();

    void updateUserProfileInfo(UserProfileDto userProfileDto);

    String uploadUserPhoto(MultipartFile multipartFile) throws IOException;

    void updateUserPassword(ChangePasswordDto passwordDto);

    void deleteUser();

    User fetchUserById(Long id);

}
