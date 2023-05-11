package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.service.RecognizerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoiceMessageController {

    @Autowired
    private RecognizerRequestService recognizerRequestService;

    @PostMapping("/voice_message")
    public void processVoiceMessage(@RequestBody byte[] voiceMessage) {
        recognizerRequestService.sendVoiceMessage(voiceMessage);
    }
}
