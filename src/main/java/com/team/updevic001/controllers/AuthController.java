package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.security.AuthRequestDto;
import com.team.updevic001.model.dtos.request.security.OtpRequest;
import com.team.updevic001.model.dtos.request.security.RefreshTokenRequest;
import com.team.updevic001.model.dtos.request.security.RegisterRequest;
import com.team.updevic001.model.dtos.request.security.ResetPasswordRequest;
import com.team.updevic001.model.dtos.request.security.VerifyCodeRequest;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.VerifyCodeResponse;
import com.team.updevic001.model.dtos.response.video.FileUploadResponse;
import com.team.updevic001.services.impl.user.UserServiceImpl;
import com.team.updevic001.services.interfaces.AuthService;
import com.team.updevic001.services.interfaces.FileLoadService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {


    AuthService authService;

    @PutMapping(path = "create-admin")
    @ResponseStatus(CREATED)
    public AuthResponseDto createUserWithAdminRole(AuthRequestDto authRequest) {
        return authService.createUserWithAdminRole(authRequest);
    }

    @GetMapping
    public ResponseUserDto getLoggedInUser() {
        return authService.getLoggedInUser();
    }

    @PostMapping("/sign-up")
    @ResponseStatus(NO_CONTENT)
    public void registerUser(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/verify-otp")
    public AuthResponseDto verifyOtpAndGenerateToken(@RequestBody OtpRequest request) {
        return authService.verifyOtpAndGenerateToken(request);
    }

    @PostMapping("/sign-in")
    public AuthResponseDto login(
            @RequestBody AuthRequestDto authRequest
    ) {
        return authService.login(authRequest);
    }

    @GetMapping("/auth/oauth-success")
    public void oauthSuccess(@RequestParam String accessToken, HttpServletResponse response) throws IOException {

    }

    @PostMapping("/forgot-password")
    @ResponseStatus(NO_CONTENT)
    public void requestPasswordReset(@RequestParam @NotBlank String email) {
        authService.requestPasswordReset(email);
    }

    @PostMapping("/verify-code")
    public VerifyCodeResponse verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        return authService.verifyCode(request);
    }

    @PatchMapping("/reset-password")
    @ResponseStatus(NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
    }

//    /**
//     *  Şifrə reset endpoint
//     */
//    @PatchMapping("/reset-password")
//    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//        if (!request.getNewPassword().equals(request.getRetryPassword())) {
//            return ResponseEntity.badRequest().build(); // Password mismatch
//        }
//
//        authService.resetPassword(request.getEmail(), request.getNewPassword());
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/refresh-token")
    public AuthResponseDto refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request);
    }

}
