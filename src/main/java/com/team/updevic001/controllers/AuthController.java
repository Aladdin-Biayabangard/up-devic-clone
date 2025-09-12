package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.request.security.*;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.services.interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/forgot-password")
    @ResponseStatus(NO_CONTENT)
    public void requestPasswordReset(@RequestParam @NotBlank String email) {
        authService.requestPasswordReset(email);
    }

    @PatchMapping("/reset-password")
    @ResponseStatus(NO_CONTENT)
    public void resetPassword(@RequestParam @NotBlank String token, @Valid @RequestBody RecoveryPassword recoveryPassword) {
        authService.resetPassword(token, recoveryPassword);
    }

    @PostMapping("/refresh-token")
    public AuthResponseDto refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request);
    }

}
