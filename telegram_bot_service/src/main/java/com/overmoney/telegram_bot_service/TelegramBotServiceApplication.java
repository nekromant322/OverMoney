package com.overmoney.telegram_bot_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class TelegramBotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotServiceApplication.class, args);
    }
}