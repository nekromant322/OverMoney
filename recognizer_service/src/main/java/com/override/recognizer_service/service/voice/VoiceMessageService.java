package com.override.recognizer_service.service.voice;

import com.override.dto.AudioRecognizerGoRequestDTO;
import com.override.dto.VoiceMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class VoiceMessageService {

    @Autowired
    private WordsToNumbersService wordsToNumbersService;

    @Autowired
    private VoiceDTORecognitionService voiceDTORecognitionService;

    public String processVoiceMessage(VoiceMessageDTO voiceMessage) {
        AudioRecognizerGoRequestDTO requestDTO = mapRequest(voiceMessage);
        String textMessage = voiceDTORecognitionService.voiceToText(requestDTO);
        return wordsToNumbersService.wordsToNumbers(textMessage);
    }

    private AudioRecognizerGoRequestDTO mapRequest(VoiceMessageDTO voiceMessage) {
        UUID id = UUID.randomUUID();
        return AudioRecognizerGoRequestDTO.builder()
                .id(id)
                .voiceMessage(voiceMessage.getVoiceMessageBytes())
                .build();
    }
}
