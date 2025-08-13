package com.team.updevic001.configuration.config.security;

import com.team.updevic001.configuration.enums.AuthMapping;
import com.team.updevic001.model.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Value("${frontend.url1}")
    String FRONTEND_URL1;

    @Value("${frontend.url2}")
    String FRONTEND_URL2;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(FRONTEND_URL1, FRONTEND_URL2));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","ACCEPT"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))

                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.GET, AuthMapping.PERMIT_ALL_GET.getUrls()).permitAll()
                                .requestMatchers(AuthMapping.PERMIT_ALL.getUrls()).permitAll()
                                .requestMatchers(AuthMapping.ADMIN.getUrls()).hasAnyRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, AuthMapping.TEACHER_ADMIN_DELETE.getUrls()).hasAnyRole(Role.ADMIN.name(), Role.TEACHER.name())
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                        )
                        .accessDeniedHandler(((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage())))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;
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
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}