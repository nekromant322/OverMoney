package com.override.recognizer_service.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "deepseek.options")
public class DeepSeekOptionsProperties {
    Float temperature;
    Float topP;
    Float repetitionPenalty;
}
