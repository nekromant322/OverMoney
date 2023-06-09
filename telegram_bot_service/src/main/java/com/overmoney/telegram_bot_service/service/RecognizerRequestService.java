package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.RecognizerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecognizerRequestService {

    @Autowired
    private RecognizerFeign recognizerFeign;

    public String sendVoiceMessage(byte[] voiceMessage) {
        return recognizerFeign.sendVoiceMessage(voiceMessage);
    }
}
