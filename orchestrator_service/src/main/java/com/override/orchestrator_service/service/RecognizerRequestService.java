package com.override.orchestrator_service.service;

import com.override.orchestrator_service.feign.RecognizerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecognizerRequestService {

    @Autowired
    RecognizerFeign recognizerFeign;

    public void sendVoiceMessage(byte[] voiceMessage) {
        recognizerFeign.sendVoiceMessage(voiceMessage);
    }
}
