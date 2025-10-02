package com.team.updevic001.configuration.config.sendgrid;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid("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");
    }
}
