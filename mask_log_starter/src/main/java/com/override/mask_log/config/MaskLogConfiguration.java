package com.override.mask_log.config;

import com.override.mask_log.impl.formatter.MaskLogFormatter;
import com.override.mask_log.impl.http.CustomSink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.*;

import static org.zalando.logbook.Conditions.*;

@Configuration
@EnableConfigurationProperties(MaskLogProperties.class)
@ComponentScan(basePackages = "com.override.mask_log")
public class MaskLogConfiguration {
    @Autowired
    private MaskLogFormatter maskLogFormatter;

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(exclude(
                        requestTo("/actuator/**"),
                        requestTo("/scripts/**"),
                        requestTo("/css/**")))
                .sink(new CustomSink(maskLogFormatter, new DefaultHttpLogWriter()))
                .build();
    }
}