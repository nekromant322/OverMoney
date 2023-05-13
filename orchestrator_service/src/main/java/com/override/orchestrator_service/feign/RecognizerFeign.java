package com.override.orchestrator_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="recognizer-service")
public interface RecognizerFeign {



    @GetMapping("/recognizer")
    String getRecognition();
















    @PostMapping("/voice_message")
    void sendVoiceMessage(byte[] voiceMessage);

}
