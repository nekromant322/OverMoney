//package com.override.recognizer_service.config;
//
//import io.github.givimad.whisperjni.*;
//import lombok.SneakyThrows;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.nio.file.Path;
//
//@Configuration
//public class WhisperConfig {
//
//    private final String MODEL_NAME = "ggml-large-v1.bin";
//
//    @Bean
//    public WhisperJNI getWhisper() throws IOException {
//        WhisperJNI whisper = new WhisperJNI();
//        whisper.loadLibrary();
//        return whisper;
//    }
//
//    @Bean
//    public WhisperFullParams getWhisperFullParams() {
//        return new WhisperFullParams(WhisperSamplingStrategy.BEAN_SEARCH);
//    }
//
//    @Bean
//    @SneakyThrows
//    public WhisperContext getContext() {
//        return getWhisper().initNoState(Path.of(MODEL_NAME));
//    }
//}