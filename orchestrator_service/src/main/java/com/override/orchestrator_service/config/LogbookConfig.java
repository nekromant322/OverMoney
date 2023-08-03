package com.override.orchestrator_service.config;

import com.override.orchestrator_service.util.MyJsonLogFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.*;

import static org.zalando.logbook.Conditions.*;

@Configuration
public class LogbookConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(exclude(requestTo("/actuator/**")))
                .sink(new DefaultSink(new MyJsonLogFormatter(), new DefaultHttpLogWriter()))
                .build();
    }
}
