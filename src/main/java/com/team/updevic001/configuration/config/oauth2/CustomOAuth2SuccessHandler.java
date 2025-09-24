package com.team.updevic001.configuration.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.model.dtos.response.AuthResponseDto;
import com.team.updevic001.services.interfaces.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final UserRepository userRepository;
    private final AuthService authService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth = (OAuth2User) authentication.getPrincipal();
        User user = userRepository.findByEmail(oauth.getAttribute("email")).orElseThrow();

        AuthResponseDto authResponseDto = authService.buildAuthResponse(user);

        Cookie refreshCookie = new Cookie("refreshToken", String.valueOf(authResponseDto.getRefreshToken()));
        Cookie accessCookie = new Cookie("accessToken", String.valueOf(authResponseDto.getAccessToken()));
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(authResponseDto));

    }

}
