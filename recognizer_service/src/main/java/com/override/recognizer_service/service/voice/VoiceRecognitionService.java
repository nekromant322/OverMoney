package com.override.recognizer_service.service.voice;

public interface VoiceRecognitionService {

    String voiceToText(byte[] voiceMessage);
}
