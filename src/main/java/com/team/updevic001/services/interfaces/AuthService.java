package com.team.updevic001.services.interfaces;

import com.team.updevic001.dao.entities.auth.RefreshToken;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.auth.UserRole;
import com.team.updevic001.model.dtos.request.security.*;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.VerifyCodeResponse;
import com.team.updevic001.model.enums.Role;


public interface AuthService {

    AuthResponseDto createUserWithAdminRole(AuthRequestDto authRequest);

    ResponseUserDto getLoggedInUser();

    void register(RegisterRequest request);

    AuthResponseDto login(AuthRequestDto authRequestDto);

    AuthResponseDto verifyOtpAndGenerateToken(OtpRequest request);

    VerifyCodeResponse verifyCode(VerifyCodeRequest request);

    void requestPasswordReset(String email);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    AuthResponseDto refreshAccessToken(RefreshTokenRequest request);

    UserRole findOrCreateRole(Role role);

    AuthResponseDto buildAuthResponse(User user);

    RefreshToken createAndSaveRefreshToken(User user);

    AuthResponseDto buildAuthResponse(User user, Long refreshTokenId);

    UserRole createOrFetchRole(Role role);
}