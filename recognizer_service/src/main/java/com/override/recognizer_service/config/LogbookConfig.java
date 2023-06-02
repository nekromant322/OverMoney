package com.override.recognizer_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;

@Configuration
public class LogbookConfig {
    // sample text
    @Bean
    public Logbook logbook() {
        return Logbook.builder().sink(new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter())).build();
    }
}
