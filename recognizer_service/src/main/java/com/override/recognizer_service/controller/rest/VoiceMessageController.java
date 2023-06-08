package com.override.recognizer_service.controller.rest;

import com.override.recognizer_service.service.VoiceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class VoiceMessageController {

    @Autowired
    private VoiceMessageService voiceMessageService;

    // voiceMessageBytes - байты файла голосового сообщения в формате .ogg, учитывайте это при дальнейшей конвертации
    @PostMapping("/voice_message")
    public String processVoiceMessage(@RequestBody byte[] voiceMessage) throws IOException, InterruptedException {
        log.info("Received voice message");
        return voiceMessageService.processVoiceMessage(voiceMessage);
    }
}
