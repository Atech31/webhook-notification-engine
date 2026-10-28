package com.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebhookNotificationEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookNotificationEngineApplication.class, args);
    }
}
