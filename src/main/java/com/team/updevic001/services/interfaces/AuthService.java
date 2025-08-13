package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.request.security.*;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;


public interface AuthService {

    AuthResponseDto createUserWithAdminRole(AuthRequestDto authRequest);

    ResponseUserDto getLoggedInUser();

    void register(RegisterRequest request);

    AuthResponseDto login(AuthRequestDto authRequestDto);

    AuthResponseDto verifyAndGetToken(OtpRequest request);

    void requestPasswordReset(String email);

    void resetPassword(String token, RecoveryPassword recoveryPassword);

    AuthResponseDto refreshAccessToken(RefreshTokenRequest request);
}