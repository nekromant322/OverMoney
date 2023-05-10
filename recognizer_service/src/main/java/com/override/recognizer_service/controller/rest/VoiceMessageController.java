package com.override.recognizer_service.controller.rest;

import com.override.recognizer_service.util.ConverterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
public class VoiceMessageController {

    @Autowired
    ConverterUtils converterUtils;

    @PostMapping("/voice_message")
    public void processVoiceMessage(@RequestBody byte[] voiceMessageBytes) throws IOException {
        File voiceMessage = converterUtils.convertByteArrayToOgg(voiceMessageBytes);
        log.info("Received and converted voice message " + voiceMessage.getName());
    }
}
