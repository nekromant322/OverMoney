package com.override.recognizer_service.service;

import com.override.dto.VoiceMessageDTO;
import com.override.recognizer_service.service.voice.VoiceRecognitionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VoiceMessageService {

    @Autowired
    private VoiceRecognitionService voiceRecognitionService;
    @Autowired
    private WordsToNumbersService wordsToNumbersService;

    private final String VOICE_FILE_NAME = "voiceMessage";
    private final String OGG_FORMAT = ".ogg";
    private final String WAV_FORMAT = ".wav";

    @SneakyThrows
    public String processVoiceMessage(VoiceMessageDTO voiceMessage) {
        Long chatId = voiceMessage.getChatId();
        Long userId = voiceMessage.getUserId();
        byte[] voiceMessageBytes = voiceMessage.getVoiceMessageBytes();

        File wavVoiceFile = convertOggBytesToWav(voiceMessageBytes, userId, chatId);

        String messageFullText = voiceRecognitionService.voiceToText(wavVoiceFile);

        if (wavVoiceFile.delete()) {
            log.info("WAV voice file has been deleted by user with userId " + userId + " and chatId " + chatId);
        } else {
            throw new IOException("WAV voice file was not deleted by user with userId " + userId + " and chatId " + chatId);
        }

        return wordsToNumbersService.wordsToNumbers(messageFullText);
    }

    @SneakyThrows
    private File convertOggBytesToWav(byte[] voiceMessage, Long userId, Long chatId) {
        UUID voiceId = UUID.randomUUID();
        StringBuilder oggFileName = new StringBuilder();
        oggFileName
                .append(VOICE_FILE_NAME)
                .append(voiceId)
                .append(OGG_FORMAT);
        File oggVoiceFile = new File(oggFileName.toString());
        if (oggVoiceFile.createNewFile()) {
            log.info("OGG voice file has been created by user with userId " + userId + " and chatId " + chatId);
        } else {
            throw new IOException("OGG voice file was not created by user with userId " + userId + " and chatId " + chatId);
        }
        OutputStream outStream = new FileOutputStream(oggVoiceFile);
        outStream.write(voiceMessage);
        String wavFileName = oggFileName.toString().replace(OGG_FORMAT, WAV_FORMAT);
        String cmd = "ffmpeg -i " + oggFileName + " " + wavFileName + " -y";
        Process converting = Runtime.getRuntime().exec(cmd);
        if (!converting.waitFor(10, TimeUnit.SECONDS)) {
            converting.destroy();
            log.error("Convert voice message Timeout Occurred by user with userId " + userId + " and chatId " + chatId);
        }
        if (oggVoiceFile.delete()) {
            log.info("OGG voice file has been deleted by user with userId " + userId + " and chatId " + chatId);
        } else {
            throw new IOException("OGG voice file was not deleted by user with userId " + userId + " and chatId " + chatId);
        }

        return new File(wavFileName);
    }
}
