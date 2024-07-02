package com.override.invest_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvestConfig {

    @Bean
    public ObjectMapper getNewObjectMapper() {
        return new ObjectMapper();
    }
}