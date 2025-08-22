package com.team.updevic001.services.impl;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.enums.ExceptionConstants;
import com.team.updevic001.model.mappers.UserMapper;
import com.team.updevic001.model.mappers.UserProfileMapper;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.model.dtos.request.UserProfileDto;
import com.team.updevic001.model.dtos.request.security.ChangePasswordDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserProfileDto;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.services.interfaces.FileLoadService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.team.updevic001.model.enums.ExceptionConstants.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;
    private final FileLoadService fileLoadServiceImpl;

    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public void updateUserProfileInfo(UserProfileDto userProfileDto) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        UserProfile userProfile = userProfileRepository.findByUser(authenticatedUser);
        UserProfile updatedUserProfile = userProfileMapper.toEntity(userProfile, userProfileDto);
        userProfileRepository.save(updatedUserProfile);
    }

    @Override
    @Transactional
    public void updateUserPassword(ChangePasswordDto passwordDto) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), authenticatedUser.getPassword()) ||
            !passwordDto.getNewPassword().equals(passwordDto.getRetryPassword())) {
            throw new IllegalArgumentException("Password incorrect!");
        }
        authenticatedUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(authenticatedUser);
    }

    @Override
    @Transactional
    public ResponseUserProfileDto getUserProfile() {
        User user = authHelper.getAuthenticatedUser();
        UserProfile userProfile = userProfileRepository.findByUser(user);
        List<String> roles = extractRoleNames(user);
        return userMapper.toUserProfileDto(user.getFirstName(), user.getLastName(), userProfile, roles);
    }

    @Override
    public ResponseUserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(),
                USER_NOT_FOUND.getMessage().formatted(id)));
        return userMapper.toResponse(user, ResponseUserDto.class);
    }

    @Override
    public List<ResponseUserDto> getUser(String query) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCase(query);
        if (!users.isEmpty()) {
            return users.stream().map(userMapper::toResponseFromUser).toList();
        } else {
            throw new NotFoundException(USER_NOT_FOUND.getCode(),
                    USER_NOT_FOUND.getMessage().formatted(query));
        }
    }

    @Override
    public void uploadUserPhoto(MultipartFile multipartFile) throws IOException {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        String photoOfWhat = "profilePhoto";
        FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, authenticatedUser.getId().toString(), photoOfWhat);
        userProfileRepository.updateCourseFileInfo(
                authenticatedUser.getId(),
                fileUploadResponse.getKey(),
                fileUploadResponse.getUrl());
    }

    @Override
    @Transactional
    public void deleteUser() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        userRepository.updateUserStatus(authenticatedUser.getId(), Status.DELETED);
    }

    public User fetchUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(),
                USER_NOT_FOUND.getMessage().formatted(id)));
    }

    private List<String> extractRoleNames(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().toString())
                .toList();
    }
}
