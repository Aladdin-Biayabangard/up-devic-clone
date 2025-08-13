package com.team.updevic001;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class UpDevicApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpDevicApplication.class, args);

    }

}
