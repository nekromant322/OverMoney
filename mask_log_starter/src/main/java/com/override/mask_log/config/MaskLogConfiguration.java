package com.override.mask_log.config;

import com.override.mask_log.formatter.MaskLogFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;

import static org.zalando.logbook.Conditions.exclude;
import static org.zalando.logbook.Conditions.requestTo;

@Configuration
@EnableConfigurationProperties(MaskLogProperties.class)
@ComponentScan(basePackages = "com.override.mask_log")
public class MaskLogConfiguration {
    @Autowired
    MaskLogFormatter maskLogFormatter;

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(exclude(requestTo("/actuator/**")))
                .sink(new DefaultSink(maskLogFormatter, new DefaultHttpLogWriter()))
                .build();
    }
}