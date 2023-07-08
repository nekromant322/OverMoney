package com.override.recognizer_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "audio-recognizer-go-service")
@Getter
@Setter
public class AudioRecognizerGoServiceProperties {
    private String url;
    private String method;
    private String contentType;
    private String accept;
}
