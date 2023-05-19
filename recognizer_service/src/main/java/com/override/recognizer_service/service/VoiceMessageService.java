package com.override.recognizer_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VoiceMessageService {
    private static final String VOICE_FILE_NAME = "voiceMessage";
    private static final String OGG_FORMAT = ".ogg";
    private static final String WAV_FORMAT = ".wav";

    public byte[] convertOggBytesToWav(byte[] voiceMessage) throws IOException, InterruptedException {
        UUID voiceId = UUID.randomUUID();
        StringBuilder oggFileName = new StringBuilder();
        oggFileName
                .append(VOICE_FILE_NAME)
                .append(voiceId)
                .append(OGG_FORMAT);
        File oggVoiceFile = new File(oggFileName.toString());
        if (oggVoiceFile.createNewFile()) {
            log.info("OGG voice file has been created");
        } else {
            log.error("OGG voice file was not created");
        }
        OutputStream outStream = new FileOutputStream(oggVoiceFile);
        outStream.write(voiceMessage);
        String wavFileName = oggFileName.toString().replace(OGG_FORMAT, WAV_FORMAT);
        String cmd = "ffmpeg -i " + oggFileName + " " + wavFileName + " -y";
        Process converting = Runtime.getRuntime().exec(cmd);
        if (!converting.waitFor(10, TimeUnit.SECONDS)) {
            converting.destroy();
            log.error("Convert voice message Timeout Occurred");
        }
        if (oggVoiceFile.delete()) {
            log.info("OGG voice file has been deleted");
        } else {
            log.warn("OGG voice file was not deleted");
        }
        byte[] wavVoiceBytes = null;
        try (InputStream in = new FileInputStream(wavFileName)) {
            wavVoiceBytes = in.readAllBytes();
            log.info("OGG bytes have been converted to WAV");
        } catch (IOException e) {
            log.error("WAV voice file bytes not written");
        }
        File wavVoiceFile = new File(wavFileName);

        if (wavVoiceFile.delete()) {
            log.info("WAV voice file has been deleted");
        } else {
            log.warn("WAV voice file was not deleted");
        }
        return wavVoiceBytes;
    }
}
