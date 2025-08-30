package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.UserProfileDto;
import com.team.updevic001.model.dtos.request.security.ChangePasswordDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserProfileDto;
import com.team.updevic001.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping()
    public void updateUserProfileInfo(@RequestBody UserProfileDto userProfileDto) {
        userService.updateUserProfileInfo(userProfileDto);
    }

    @PatchMapping("/password")
    public void updateUserPassword(@Valid @RequestBody ChangePasswordDto passwordDto) {
        userService.updateUserPassword(passwordDto);
    }

    @PatchMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadUserPhoto(@RequestPart MultipartFile multipartFile) throws IOException {
        userService.uploadUserPhoto(multipartFile);
    }

    @GetMapping(path = "/profile")
    @Operation(summary = "Profili göstərmək üçün")
    public ResponseEntity<ResponseUserProfileDto> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable Long id) {
        ResponseUserDto userById = userService.getUserById(id);
        return new ResponseEntity<>(userById, HttpStatus.OK);
    }

}
