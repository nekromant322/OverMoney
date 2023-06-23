package com.override.recognizer_service.service.voice;

import java.io.File;

public interface VoiceRecognitionService {

    String voiceToText(File voiceMessage);
}