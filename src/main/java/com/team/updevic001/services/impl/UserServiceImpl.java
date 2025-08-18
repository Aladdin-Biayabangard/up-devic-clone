package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.UserMapper;
import com.team.updevic001.configuration.mappers.UserProfileMapper;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.ResourceNotFoundException;
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
        return userMapper.toUserProfileDto(user.getFirstName(), user.getLastName(), userProfile);
    }

    @Override
    public ResponseUserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found Exception!"));
        return userMapper.toResponse(user, ResponseUserDto.class);
    }

    @Override
    public List<ResponseUserDto> getUser(String query) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCase(query);
        if (!users.isEmpty()) {
            return users.stream().map(userMapper::toResponseFromUser).toList();
        } else {
            throw new ResourceNotFoundException("There is no user with this name.");
        }
    }

    @Override
    public String uploadUserPhoto(MultipartFile multipartFile) throws IOException {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        String photoOfWhat = "profilePhoto";
        userProfileRepository.findProfilePhotoKeyBy(authenticatedUser).ifPresent(fileLoadServiceImpl::deleteFileFromAws);
        FileUploadResponse fileUploadResponse = fileLoadServiceImpl.uploadFile(multipartFile, authenticatedUser.getId().toString(), photoOfWhat);
        userProfileRepository.updateCourseFileInfo(
                authenticatedUser.getId(),
                fileUploadResponse.getKey(),
                fileUploadResponse.getUrl());
        return fileUploadResponse.getUrl();
    }

    @Override
    @Transactional
    public void deleteUser() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        userRepository.updateUserStatus(authenticatedUser.getId(), Status.DELETED);
    }

    public User fetchUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
