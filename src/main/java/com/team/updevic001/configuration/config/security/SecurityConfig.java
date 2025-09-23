package com.team.updevic001.configuration.config.security;

import com.team.updevic001.configuration.config.oauth2.CaptureRedirectParamFilter;
import com.team.updevic001.configuration.config.oauth2.CustomOAuth2SuccessHandler;
import com.team.updevic001.configuration.config.oauth2.CustomOAuth2UserService;
import com.team.updevic001.configuration.enums.ApiEndpoint;
import com.team.updevic001.configuration.enums.ApiSecurityLevel;
import com.team.updevic001.model.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.net.URI;
import java.util.List;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;

    @Value("${frontend.url1}")
    String FRONTEND_URL1;
    @Value("${frontend.url2}")
    String FRONTEND_URL2;
    @Value("${frontend.url3}")
    String FRONTEND_URL3;
    @Value("${frontend.url4}")
    String FRONTEND_URL4;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService, CustomOAuth2SuccessHandler customOAuth2SuccessHandler) throws Exception {

        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(FRONTEND_URL1, FRONTEND_URL2, FRONTEND_URL3, FRONTEND_URL4));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> {

                    authorize.requestMatchers("/oauth2/**", "/login/**").permitAll();
                    // PUBLIC (permit all)
                    for (ApiEndpoint endpoint : ApiEndpoint.values()) {
                        if (endpoint.getSecurityLevel() == ApiSecurityLevel.PUBLIC) {
                            if (endpoint.getHttpMethod() == null) {
                                authorize.requestMatchers(endpoint.getPathPattern()).permitAll();
                            } else {
                                authorize.requestMatchers(endpoint.getHttpMethod(), endpoint.getPathPattern()).permitAll();
                            }
                        }
                    }

                    // ADMIN
                    for (ApiEndpoint endpoint : ApiEndpoint.values()) {
                        if (endpoint.getSecurityLevel() == ApiSecurityLevel.ADMIN) {
                            if (endpoint.getHttpMethod() == null) {
                                authorize.requestMatchers(endpoint.getPathPattern()).hasRole(Role.ADMIN.name());
                            } else {
                                authorize.requestMatchers(endpoint.getHttpMethod(), endpoint.getPathPattern()).hasRole(Role.ADMIN.name());
                            }
                        }
                    }

                    // TEACHER
                    for (ApiEndpoint endpoint : ApiEndpoint.values()) {
                        if (endpoint.getSecurityLevel() == ApiSecurityLevel.TEACHER) {
                            if (endpoint.getHttpMethod() == null) {
                                authorize.requestMatchers(endpoint.getPathPattern()).hasRole(Role.TEACHER.name());
                            } else {
                                authorize.requestMatchers(endpoint.getHttpMethod(), endpoint.getPathPattern()).hasRole(Role.TEACHER.name());
                            }
                        }
                    }

                    // STUDENT (və eyni zamanda login olan digər rollar)
                    for (ApiEndpoint endpoint : ApiEndpoint.values()) {
                        if (endpoint.getSecurityLevel() == ApiSecurityLevel.STUDENT) {
                            if (endpoint.getHttpMethod() == null) {
                                authorize.requestMatchers(endpoint.getPathPattern())
                                        .hasAnyRole(Role.STUDENT.name(), Role.TEACHER.name(), Role.ADMIN.name());
                            } else {
                                authorize.requestMatchers(endpoint.getHttpMethod(), endpoint.getPathPattern())
                                        .hasAnyRole(Role.STUDENT.name(), Role.TEACHER.name(), Role.ADMIN.name());
                            }
                        }
                    }

                    // Digər bütün requestlər authenticated olsun
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage())
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    AuthenticationSuccessHandler successHandler() {
        var fallback = new SavedRequestAwareAuthenticationSuccessHandler();
        fallback.setDefaultTargetUrl("/"); // backend fallback if no redirect is provided

        return (request, response, authentication) -> {
            HttpSession session = request.getSession(false);
            String target = (session != null) ? (String) session.getAttribute(CaptureRedirectParamFilter.ATTR) : null;
            if (session != null) session.removeAttribute(CaptureRedirectParamFilter.ATTR);

            if (isAllowed(target)) {
                response.sendRedirect(target);
            } else {
                fallback.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }

    private boolean isAllowed(String url) {
        if (url == null || url.isBlank()) return false;
        try {
            URI u = URI.create(url);
            String host = u.getHost();
            String scheme = u.getScheme();
            if (host == null || scheme == null) return false;

            boolean schemeOk = scheme.equals("https") || (scheme.equals("http") && (host.equals("localhost") || host.equals("127.0.0.1")));
            if (!schemeOk) return false;

            return securityProperties.getAllowedRedirectHosts().stream().anyMatch(allowed ->
                    host.equalsIgnoreCase(allowed) || host.toLowerCase().endsWith("." + allowed.toLowerCase()));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
