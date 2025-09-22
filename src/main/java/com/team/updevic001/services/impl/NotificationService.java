package com.team.updevic001.services.impl;

import com.team.updevic001.mail.EmailServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailServiceImpl emailServiceImpl;

    @PostConstruct
    public void testEmailSend(){
        Map<String, Object> variables = Map.of("userName", "Aladdin","code","897951");
        emailServiceImpl.sendHtmlEmail("aladdin19.11.21@gmail.com", "application-info.html", variables);


        emailServiceImpl.sendHtmlEmail("aladdin19.11.21@gmail.com", "verification.html", variables);
//        emailServiceImpl.sendHtmlEmail("aladdin19.11.21@gmail.com", "application-info.html", variables);
//        emailServiceImpl.sendHtmlEmail("aladdin19.11.21@gmail.com", "application-info.html", variables);
//        emailServiceImpl.sendHtmlEmail("aladdin19.11.21@gmail.com", "application-info.html", variables);


    }
}
