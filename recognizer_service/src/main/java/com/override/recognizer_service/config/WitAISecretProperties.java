package com.override.recognizer_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wit-ai.secret")
@Getter
@Setter
public class WitAISecretProperties {
    private String url;
    private String token;
    private String version;
}
