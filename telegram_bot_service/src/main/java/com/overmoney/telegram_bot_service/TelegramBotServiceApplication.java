package com.overmoney.telegram_bot_service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.logging.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class TelegramBotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotServiceApplication.class, args);
    }

}