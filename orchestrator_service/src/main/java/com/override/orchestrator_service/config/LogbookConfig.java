package com.override.orchestrator_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import static org.zalando.logbook.Conditions.exclude;
import static org.zalando.logbook.Conditions.requestTo;
import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
public class LogbookConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(exclude(requestTo("/actuator/**")))
                .bodyFilter(jsonPath("$.token").replace("X"))
                .bodyFilter(jsonPath("$.accountId").replace("X"))
                .sink(new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter()))
                .build();
    }
}
