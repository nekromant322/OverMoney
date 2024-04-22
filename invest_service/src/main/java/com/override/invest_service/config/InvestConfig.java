package com.override.invest_service.config;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Configuration
public class InvestConfig {

    @Bean
    public ObjectMapper getNewObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @DependsOn({"getNewObjectMapper"})
    public ObjectWriter getNewObjectWritter(@Autowired ObjectMapper objectMapper) {
        return objectMapper.writer(new DefaultPrettyPrinter());
    }
}