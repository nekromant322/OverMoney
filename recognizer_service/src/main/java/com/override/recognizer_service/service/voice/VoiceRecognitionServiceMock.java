package com.override.recognizer_service.service.voice;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class VoiceRecognitionServiceMock implements VoiceRecognitionService {

    @Override
    public String voiceToText(byte[] voiceMessage) {
        return "пиво 200";
    }
}
