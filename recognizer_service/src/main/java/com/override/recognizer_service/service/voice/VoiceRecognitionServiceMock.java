package com.override.recognizer_service.service.voice;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Profile("dev")
public class VoiceRecognitionServiceMock implements VoiceRecognitionService {

    @Override
    public String voiceToText(File voiceMessage) {
        return "пиво двести";
    }
}