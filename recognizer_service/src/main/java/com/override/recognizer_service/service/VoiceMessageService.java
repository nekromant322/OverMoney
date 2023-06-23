package com.override.recognizer_service.service;

import com.override.recognizer_service.service.voice.VoiceRecognitionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;

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

    public String processVoiceMessage(byte[] voiceMessage) {
        String messageFullText = convertOggBytesToWavAndRecognizeText(voiceMessage);
        return wordsToNumbersService.wordsToNumbers(messageFullText);
    }

    @SneakyThrows
    public String convertOggBytesToWavAndRecognizeText(byte[] voiceMessage) {
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
        File wavVoiceFile = new File(wavFileName);

        String text = voiceRecognitionService.voiceToText(wavVoiceFile);

        if (wavVoiceFile.delete()) {
            log.info("WAV voice file has been deleted");
        } else {
            throw new IOException("WAV voice file was not deleted");
        }

        return text;
    }


    @SneakyThrows
    public static void main(String[] args) {
        new VoiceMessageService().convertVoiceToText(null);
    }

    @SneakyThrows
    private String convertVoiceToText(byte[] voiceMessageWav) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\ПК\\IdeaProjects\\OverMoney\\recognizer_service\\src\\main\\java\\com\\override\\recognizer_service\\service\\Recording.wav");
        Model model = new Model("C:\\Users\\ПК\\IdeaProjects\\OverMoney\\recognizer_service\\src\\main\\java\\com\\override\\recognizer_service\\service\\vosk-model-small-ru-0.22"); // <- тут абсолютный путь, если не получится, посмотри демку на гите
        InputStream ais = AudioSystem.getAudioInputStream(fileInputStream);
        Recognizer recognizer = new Recognizer(model, 16000);

        int nbytes;
        byte[] b = new byte[4096];
        while ((nbytes = ais.read(b)) >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                log.info(recognizer.getResult());
            } else {
                log.info(recognizer.getPartialResult());
            }
        }
        System.out.println(recognizer.getFinalResult());
        log.info(recognizer.getFinalResult());

        return "заглушка 5000";
    }
}
