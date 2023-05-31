package com.override.orchestrator_service.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recent-activity")
@Getter
@Setter
public class RecentActivityProperties {
    private long activityDays;
}
