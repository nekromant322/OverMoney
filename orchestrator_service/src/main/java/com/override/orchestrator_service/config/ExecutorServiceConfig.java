package com.override.orchestrator_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    @Primary
    public ExecutorService getNewSingleThread() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService diffWidgetExecutor() {
        return Executors.newFixedThreadPool(3);
    }
}
