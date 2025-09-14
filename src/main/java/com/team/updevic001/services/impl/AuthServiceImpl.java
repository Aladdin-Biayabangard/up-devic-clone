package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.PasswordResetToken;
import com.team.updevic001.dao.entities.RefreshToken;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserProfile;
import com.team.updevic001.dao.entities.UserRole;
import com.team.updevic001.dao.repositories.PasswordResetTokenRepository;
import com.team.updevic001.dao.repositories.RefreshTokenRepository;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.dao.repositories.UserRoleRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.ExpiredRefreshTokenException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.mail.EmailTemplate;
import com.team.updevic001.model.dtos.request.security.AuthRequestDto;
import com.team.updevic001.model.dtos.request.security.OtpRequest;
import com.team.updevic001.model.dtos.request.security.RecoveryPassword;
import com.team.updevic001.model.dtos.request.security.RefreshTokenRequest;
import com.team.updevic001.model.dtos.request.security.RegisterRequest;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.model.mappers.UserMapper;
import com.team.updevic001.services.interfaces.AuthService;
import com.team.updevic001.services.interfaces.OtpService;
import com.team.updevic001.utility.AuthHelper;
import com.team.updevic001.utility.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.team.updevic001.exceptions.ExceptionConstants.ALREADY_EXISTS_EXCEPTION;
import static com.team.updevic001.exceptions.ExceptionConstants.EXPIRED_REFRESH_TOKEN_EXCEPTION;
import static com.team.updevic001.exceptions.ExceptionConstants.NOT_FOUND;
import static com.team.updevic001.exceptions.ExceptionConstants.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    UserRoleRepository userRoleRepository;
    UserProfileRepository userProfileRepository;
    OtpService otpService;
    EmailServiceImpl emailServiceImpl;
    PasswordResetTokenRepository passwordResetTokenRepository;
    RefreshTokenRepository refreshTokenRepository;
    AuthHelper authHelper;
    UserMapper userMapper;

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final long PASSWORD_RESET_EXPIRATION_MIN = 15;

    @Transactional
    @Override
    public void register(RegisterRequest request) {
        validateUserRegistration(request);
        UserRole defaultRole = findOrCreateRole(Role.STUDENT);
        User newUser = buildNewUser(request, defaultRole);
        userRepository.save(newUser);
        userProfileRepository.save(UserProfile.builder().user(newUser).build());
        otpService.sendOtp(newUser);
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequest ) {
        User user = findActiveUserByEmail(authRequest.getEmail());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
  //      loginHistoryService.saveLoginHistory(user);
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponseDto verifyOtpAndGenerateToken(OtpRequest request) {

        User user = userRepository.findByEmailAndStatus(request.getEmail(), Status.PENDING)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(),
                        USER_NOT_FOUND.getMessage().formatted(request.getEmail())));

        otpService.verifyOtp(request);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto createUserWithAdminRole(AuthRequestDto authRequest) {
        User user = authenticateUser(authRequest);
        UserRole userRole = createOrFetchRole(Role.ADMIN);
        if (!user.getRoles().contains(userRole)) {
            user.setRoles(new ArrayList<>());
            user.getRoles().add(userRole);
            userRepository.save(user);
        }
        return getAccessTokenAndRefreshToken(user);
    }

    @Override
    public ResponseUserDto getLoggedInUser() {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        return userMapper.toResponse(authenticatedUser, ResponseUserDto.class);
    }

    public AuthResponseDto getAccessTokenAndRefreshToken(User user) {
        String accessToken = jwtUtil.createToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getId())
                .role(extractRoleNames(user))
                .build();
    }


    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = findActiveUserByEmail(email);
        String token = authHelper.generateToken();

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expirationTime(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRATION_MIN))
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        emailServiceImpl.sendEmail(email, EmailTemplate.PASSWORD_RESET,
                Map.of("userName", user.getFirstName(), "link", token));
    }

    @Override
    @Transactional
    public void resetPassword(String token, RecoveryPassword recoveryPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .filter(t -> !t.getExpirationTime().isBefore(LocalDateTime.now()))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND.getCode(), "Reset token is invalid or expired!"));

        validatePasswordMatch(recoveryPassword.getNewPassword(), recoveryPassword.getRetryPassword());
        updateUserPassword(resetToken.getUser(), recoveryPassword.getNewPassword());
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public AuthResponseDto refreshAccessToken(RefreshTokenRequest tokenRequest) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByIdAndExpiresAtAfter(tokenRequest.getRefreshToken(), LocalDateTime.now())
                .orElseThrow(() -> new ExpiredRefreshTokenException(EXPIRED_REFRESH_TOKEN_EXCEPTION.getCode(),
                        "Refresh token expired or invalid!"));

        return buildAuthResponse(refreshToken.getUser(), refreshToken.getId());
    }


    private User findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), "User not found or inactive!"));
    }

    private void validateUserRegistration(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException(ALREADY_EXISTS_EXCEPTION.getCode(),
                    ALREADY_EXISTS_EXCEPTION.getMessage().formatted(request.getEmail()));
        }
        validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
    }

    private void validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("PASSWORD_MISMATCH");
        }
    }

    private User buildNewUser(RegisterRequest request, UserRole userRole) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .status(Status.PENDING)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .build();
    }

    private UserRole findOrCreateRole(Role role) {
        return userRoleRepository.findByName(role)
                .orElseGet(() -> userRoleRepository.save(UserRole.builder().name(role).build()));
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        return buildAuthResponse(user, createAndSaveRefreshToken(user).getId());
    }

    private AuthResponseDto buildAuthResponse(User user, Long refreshTokenId) {
        return AuthResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(jwtUtil.createToken(user)) // <-- YENİ
                .refreshToken(refreshTokenId)
                .role(extractRoleNames(user))
                .build();
    }


    private RefreshToken createAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private List<String> extractRoleNames(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().toString())
                .toList();
    }

    private User authenticateUser(AuthRequestDto authRequest) {
        return userRepository.findByEmailAndStatus(authRequest.getEmail(), Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), "User not found or inactive!"));
    }

    public UserRole createOrFetchRole(Role role) {
        return userRoleRepository.findByName(role).orElseGet(() ->
                userRoleRepository.save(UserRole.builder()
                        .name(role)
                        .build()));
    }
}

