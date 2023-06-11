package com.override.recognizer_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.vosk.Recognizer;
import org.vosk.Model;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VoiceMessageService {
    private final String VOICE_FILE_NAME = "voiceMessage";
    private final String OGG_FORMAT = ".ogg";
    private final String WAV_FORMAT = ".wav";

    public String processVoiceMessage(byte[] voiceMessage) throws IOException, InterruptedException, UnsupportedAudioFileException {
        byte[] voiceMessageWav = convertOggBytesToWav(voiceMessage);

        return convertVoiceToText(voiceMessageWav);
    }

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
            throw new IOException("OGG voice file was not created");
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
            throw new IOException("OGG voice file was not deleted");
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
            throw new IOException("WAV voice file was not deleted");
        }
        return wavVoiceBytes;
    }

    private String convertVoiceToText(byte[] voiceMessageWav) throws UnsupportedAudioFileException, IOException {

        try (Model model = new Model("model"); // <- тут абсолютный путь, если не получится, посмотри демку на гите
             InputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(voiceMessageWav));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    log.info(recognizer.getResult());
                } else {
                    log.info(recognizer.getPartialResult());
                }
            }

            log.info(recognizer.getFinalResult());
        }

        return "заглушка 5000";
    }
}
