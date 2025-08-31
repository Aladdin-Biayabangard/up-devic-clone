package com.team.updevic001.domain.applications.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email")
public class EmailConfigurationProperties {
    private String sender;
    private String subject;
    private String template;
}
