package com.override.recognizer_service.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class ConverterUtils {

    public File convertByteArrayToOgg(byte[] bytes) throws IOException {
        File voiceMessage = File.createTempFile("voice_message", ".ogg");
        StreamUtils.copy(bytes, new FileOutputStream(voiceMessage));
        return voiceMessage;
    }
}
