package com.team.updevic001.utility;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.exceptions.UnauthorizedException;
import com.team.updevic001.model.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in the security context.");
            throw new UnauthorizedException("No authenticated user found");
        }
        String authenticatedEmail = authentication.getName();
        log.debug("Authenticated user email: {}", authenticatedEmail);

        return userRepository.findByEmailAndStatus(authenticatedEmail, Status.ACTIVE)
                .orElseThrow(() -> {
                    log.error("User with email {} not found or is inactive", authenticatedEmail);
                    return new ResourceNotFoundException("USER_NOT_FOUND OR INACTIVE");
                });
    }

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}

