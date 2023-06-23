package com.override.recognizer_service.controller.rest;

import com.override.recognizer_service.service.VoiceMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoiceMessageController {

    @Autowired
    private VoiceMessageService voiceMessageService;


    @PostMapping("/voice")
    public String processVoiceMessage(@RequestBody byte[] voiceMessage) {
        return voiceMessageService.processVoiceMessage(voiceMessage);
    }
}
