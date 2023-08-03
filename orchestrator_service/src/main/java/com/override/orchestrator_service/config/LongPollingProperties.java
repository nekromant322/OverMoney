package com.override.orchestrator_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "long-polling")
@Getter
@Setter
public class LongPollingProperties {
    private List<LongPolling> overview;

    @Getter
    @Setter
    public static class LongPolling {
        private Integer activities;
        private Integer timeDelay;
    }
}
