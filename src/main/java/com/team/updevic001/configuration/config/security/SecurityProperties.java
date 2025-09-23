package com.team.updevic001.configuration.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private final JwtProperties jwtProperties = new JwtProperties();

    @Getter
    @Setter
    private List<String> allowedRedirectHosts;

    @Getter
    @Setter
    public static class JwtProperties {

        private String secret;
        private long tokenValidityInSeconds;
        private long refreshTokenValidityInSeconds;
    }
}