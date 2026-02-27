package com.override.recognizer_service.service.voice;

import com.override.dto.AudioRecognizerGoRequestDTO;

public interface VoiceDTORecognitionService {
    String voiceToText(AudioRecognizerGoRequestDTO requestDTO);
}
