package com.override.recognizer_service.service.voice;

import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.params.CBool;
import io.github.ggerganov.whispercpp.params.WhisperFullParams;
import io.github.ggerganov.whispercpp.params.WhisperSamplingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;

@Service
@Profile("!dev")
@Slf4j
public class VoiceRecognitionServiceWhisper implements VoiceRecognitionService {

    @Autowired
    private WhisperCpp whisper;

    private boolean modelInitialised = false;


    @SneakyThrows
    @Override
    public String voiceToText(byte[] voiceMessage) {
        ByteArrayInputStream voiceInputStream = new ByteArrayInputStream(voiceMessage);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(voiceInputStream);

        byte[] b = new byte[audioInputStream.available()];
        float[] floats = new float[b.length / 2];


        //todo попробовать обе стратегии
//        WhisperFullParams params = whisper.getFullDefaultParams(WhisperSamplingStrategy.WHISPER_SAMPLING_GREEDY);
        WhisperFullParams params = whisper.getFullDefaultParams(WhisperSamplingStrategy.WHISPER_SAMPLING_BEAM_SEARCH);
        params.setProgressCallback((ctx, state, progress, user_data) -> log.debug("progress: " + progress));
        params.print_progress = CBool.FALSE;
//        params.initial_prompt = "and so my fellow Americans um, like";
        try {
            audioInputStream.read(b);
            for (int i = 0, j = 0; i < b.length; i += 2, j++) {
                int intSample = (int) (b[i + 1]) << 8 | (int) (b[i]) & 0xFF;
                floats[j] = intSample / 32767.0f;
            }
            String result = whisper.fullTranscribe(params, floats);
            log.info("voice recognized: " + result);
            return result;
        } finally {
            audioInputStream.close();

        }
    }
}
