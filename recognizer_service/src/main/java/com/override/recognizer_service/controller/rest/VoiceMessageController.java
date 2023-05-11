package com.override.recognizer_service.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class VoiceMessageController {

    // voiceMessageBytes - байты файла голосового сообщения в формате .ogg, учитывайте это при дальнейшей конвертации
    @PostMapping("/voice_message")
    public void processVoiceMessage(@RequestBody byte[] voiceMessageBytes) throws IOException {
        log.info("Received voice message");
    }
}
