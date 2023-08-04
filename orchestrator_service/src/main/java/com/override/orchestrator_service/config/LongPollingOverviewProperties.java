package com.override.orchestrator_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "long-polling.overview")
@Getter
@Setter
public class LongPollingOverviewProperties {
    private Integer periodOfInactivity;
}
