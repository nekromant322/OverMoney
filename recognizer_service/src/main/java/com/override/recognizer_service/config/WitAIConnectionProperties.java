package com.override.recognizer_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wit-ai.connection")
@Getter
@Setter
public class WitAIConnectionProperties {
    private String charset;
    private String method;
    private String authProperty;
    private String contentTypeProperty;
    private String contentTypeValue;
    private String paramSeparator;
    private String versionParam;
}
