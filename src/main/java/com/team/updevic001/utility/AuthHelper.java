package com.team.updevic001.utility;

import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.exceptions.UnauthorizedException;
import com.team.updevic001.model.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

import static com.team.updevic001.exceptions.ExceptionConstants.UNAUTHORIZED_EXCEPTION;
import static com.team.updevic001.exceptions.ExceptionConstants.USER_NOT_FOUND;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found in the security context."); // error yox, warn
            throw new UnauthorizedException(UNAUTHORIZED_EXCEPTION.getCode(), "No authenticated user found");
        }

        String authenticatedEmail = authentication.getName();
        if ("anonymousUser".equals(authenticatedEmail)) {
            log.debug("Anonymous user request detected, skipping user lookup.");
            throw new UnauthorizedException(UNAUTHORIZED_EXCEPTION.getCode(), "Anonymous user not allowed");
        }

        log.debug("Authenticated user email: {}", authenticatedEmail);

        return userRepository.findByEmailAndStatus(authenticatedEmail, Status.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("User with email {} not found or is inactive", authenticatedEmail); // warn kifayətdir
                    return new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage());
                });
    }


    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}

