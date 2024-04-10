package com.override.mask_log.config;

import com.override.mask_log.impl.formatter.MaskLogFormatter;
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
                        requestTo("/css/**"),
                        contentType("text/html;charset=UTF-8")))
//                ниже вариация вместо полного отсечения логов по html
//                .responseFilter(ResponseFilters.replaceBody(message -> contentType("text/html;charset=UTF-8").test(message) ? "some HTML code" : null))
                .sink(new DefaultSink(maskLogFormatter, new DefaultHttpLogWriter()))
                .build();
    }
}