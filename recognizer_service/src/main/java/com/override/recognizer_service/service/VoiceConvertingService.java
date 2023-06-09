package com.override.recognizer_service.service;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

@Service
@Slf4j
public class VoiceConvertingService {
    @Autowired
    private WhisperJNI whisper;
    @Autowired
    private WhisperFullParams params;
    @Autowired
    private WhisperContext whisperContext;

    @SneakyThrows
    public String processWithWhisper(byte[] voiceMessage)  {
        ByteArrayInputStream audioSrc = new ByteArrayInputStream(voiceMessage);
        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new FileInputStream(new File("Recording.wav")));
        byte[] b = new byte[audioInputStream.available()];
        float[] samples = new float[b.length / 2];

        int result = whisper.full(whisperContext, params, samples, samples.length);
        if(result != 0) {
            throw new RuntimeException("Transcription failed with code " + result);
        }


        return whisper.fullGetSegmentText(whisperContext,0);
    }

//    @SneakyThrows
//    public static void main(String[] args) {
//        FileInputStream fileInputStream = new FileInputStrea("C:\\Users\\ПК\\IdeaProjects\\OverMoney\\recognizer_service\\src\\main\\java\\com\\override\\recognizer_service\\service\\Recording.wav");
//        byte[] bytes = fileInputStream.readAllBytes();
//        System.out.println("begin");
//        System.out.print("{");
//        for(int i = 0; i < bytes.length; i++) {
//            System.out.print(bytes[i]);
//            System.out.print(", ");
//        }
//        System.out.print("}");
//    }

}
