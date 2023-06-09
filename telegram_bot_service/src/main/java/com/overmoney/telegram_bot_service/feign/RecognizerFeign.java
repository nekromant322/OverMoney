package com.overmoney.telegram_bot_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="recognizer-service")
public interface RecognizerFeign {

    @PostMapping("/voice")
    String sendVoiceMessage(@RequestBody byte[] voiceMessage);
}
