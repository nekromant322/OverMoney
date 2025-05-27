package com.override.recognizer_service.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * мета информации для настройки работы модели
 * <a href="https://api-docs.deepseek.com/api/create-chat-completion">deepseek API doc</a>
 */
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "deepseek.options")
public class DeepSeekOptionsProperties {
    private Float temperature;
    private Float topP;
}
