package com.team.updevic001.configuration.config.mailjet;

import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailjetConfig {

    @Value("${mailjet.apikey}")
    private String API_KEY_MAILJET;
    @Value("${mailjet.secretkey}")
    private String SECRET_KEY_MAILJET;

    @Bean
    public MailjetClient mailjetClient() {
        // ClientOptions yaratmadan sadəcə key və secret istifadə edin
        return new MailjetClient(API_KEY_MAILJET, SECRET_KEY_MAILJET);
    }

}
