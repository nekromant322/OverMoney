package com.override.recognizer_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Primary
    @Bean
    public ExecutorService getNewDiffThread() {
        return Executors.newFixedThreadPool(3);
    }
}
