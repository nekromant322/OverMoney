package com.override.orchestrator_service.config;

import com.override.orchestrator_service.constants.Type;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "default-category")
@Getter
@Setter
public class DefaultCategoryProperties {
    private List<DefaultCategory> categories;

    @Getter
    @Setter
    public static class DefaultCategory {
        private String name;
        private Type type;
    }
}
