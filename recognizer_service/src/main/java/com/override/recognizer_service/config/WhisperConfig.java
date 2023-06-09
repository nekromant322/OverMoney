package com.override.recognizer_service.config;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class WhisperConfig {

    private final String MODEL_NAME = "ggml-model-whisper-base.bin";

    @Bean
    public WhisperJNI getWhisper() throws IOException {
        WhisperJNI whisper = new WhisperJNI();
        whisper.loadLibrary();
        return whisper;
    }

    @Bean
    public WhisperFullParams getWhisperFullParams() {
        return new WhisperFullParams();
    }

    @Bean
    @SneakyThrows
    public WhisperContext getContext() {
        return getWhisper().init(Path.of(MODEL_NAME));
    }
}