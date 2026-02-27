package com.override.recognizer_service.service.voice;

import com.override.dto.AudioRecognizerGoRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
@Slf4j
public class VoiceDTORecognitionServiceImplMock implements VoiceDTORecognitionService {
    @Override
    public String voiceToText(AudioRecognizerGoRequestDTO requestDTO) {
        return "Пиво двести";
    }
}
