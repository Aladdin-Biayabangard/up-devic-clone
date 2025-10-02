package com.team.updevic001.services.impl.common;

import com.team.updevic001.configuration.config.mailjet.MailjetEmailService;
import com.team.updevic001.dao.entities.auth.PasswordResetToken;
import com.team.updevic001.dao.entities.auth.RefreshToken;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.auth.UserProfile;
import com.team.updevic001.dao.entities.auth.UserRole;
import com.team.updevic001.dao.repositories.PasswordResetTokenRepository;
import com.team.updevic001.dao.repositories.RefreshTokenRepository;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.dao.repositories.UserRoleRepository;
import com.team.updevic001.exceptions.AlreadyExistsException;
import com.team.updevic001.exceptions.ExpiredRefreshTokenException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.request.security.AuthRequestDto;
import com.team.updevic001.model.dtos.request.security.OtpRequest;
import com.team.updevic001.model.dtos.request.security.RefreshTokenRequest;
import com.team.updevic001.model.dtos.request.security.RegisterRequest;
import com.team.updevic001.model.dtos.request.security.ResetPasswordRequest;
import com.team.updevic001.model.dtos.request.security.VerifyCodeRequest;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.VerifyCodeResponse;
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
import java.util.Optional;

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
    PasswordResetTokenRepository passwordResetTokenRepository;
    RefreshTokenRepository refreshTokenRepository;
    AuthHelper authHelper;
    UserMapper userMapper;

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final long PASSWORD_RESET_EXPIRATION_MIN = 15;
    private final MailjetEmailService mailjetEmailService;

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
    public AuthResponseDto login(AuthRequestDto authRequest) {
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
        var user = findActiveUserByEmail(email);
        var code = otpService.generateOtp(email);

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .code(code)
                .email(email)
                .expirationTime(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRATION_MIN))
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
        mailjetEmailService.sendEmail("Password reset",
                email,
                "password-reset.html",
                Map.of("userName", user.getFirstName(), "code", code), null, null);
    }

    public VerifyCodeResponse verifyCode(VerifyCodeRequest request) {
        var resetToken = passwordResetTokenRepository.findPasswordResetTokenByEmailAndCode(request.getEmail(),
                        request.getCode())
                .filter(t -> !t.getExpirationTime().isBefore(LocalDateTime.now()))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND.getCode(), "Reset token is invalid or expired!"));
        passwordResetTokenRepository.delete(resetToken);
        return new VerifyCodeResponse(true);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var user = findActiveUserByEmail(request.getEmail());
        validatePasswordMatch(request.getNewPassword(), request.getRetryPassword());
        updateUserPassword(user, request.getNewPassword());
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

    public UserRole findOrCreateRole(Role role) {
        Optional<UserRole> existingRole = userRoleRepository.findByName(role);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }
        UserRole newRole = UserRole.builder().name(role).build();
        return userRoleRepository.save(newRole);
    }


    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public AuthResponseDto buildAuthResponse(User user) {
        return buildAuthResponse(user, createAndSaveRefreshToken(user).getId());
    }

    public AuthResponseDto buildAuthResponse(User user, Long refreshTokenId) {
        return AuthResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(jwtUtil.createToken(user)) // <-- YENİ
                .refreshToken(refreshTokenId)
                .role(extractRoleNames(user))
                .build();
    }

    public RefreshToken createAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public List<String> extractRoleNames(User user) {
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

