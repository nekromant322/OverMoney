package com.override.orchestrator_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService getNewSingleThread() {
        return Executors.newSingleThreadExecutor();
    }
}
