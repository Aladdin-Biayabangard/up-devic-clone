package com.team.updevic001.configuration.config.oauth2;

import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.auth.UserProfile;
import com.team.updevic001.dao.entities.auth.UserRole;
import com.team.updevic001.dao.repositories.UserProfileRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final AuthService authService;
    private final UserProfileRepository userProfileRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(req);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        UserRole orCreateRole = authService.findOrCreateRole(Role.STUDENT);

        userRepository.findByEmail(email).orElseGet(() -> {
            User savedUser = userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // random password
                    .firstName(firstName)
                    .lastName(lastName)
                    .roles(List.of(orCreateRole))
                    .build());

            userProfileRepository.save(UserProfile.builder()
                    .user(savedUser)
                    .profilePhotoUrl(avatarUrl)
                    .profilePhotoKey(savedUser.getId() + "_profilePhoto")
                    .build());

            return savedUser;
        });
        return oAuth2User;
    }


}
