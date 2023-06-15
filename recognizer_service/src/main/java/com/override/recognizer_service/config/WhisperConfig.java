package com.override.recognizer_service.config;

import io.github.ggerganov.whispercpp.WhisperCpp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;

@Configuration
public class WhisperConfig {
    @Bean
    @ConditionalOnExpression("#{!environment.getProperty('spring.profiles.active').contains('dev')}")
    public WhisperCpp getWhisper() throws FileNotFoundException {
        // By default, models are loaded from ~/.cache/whisper/ and are usually named "ggml-${name}.bin"
        // or you can provide the absolute path to the model file.
        String modelName = "ggml-model-whisper-base.bin";
        WhisperCpp whisper = new WhisperCpp();
        whisper.initContext(modelName);
        return whisper;
    }
}
