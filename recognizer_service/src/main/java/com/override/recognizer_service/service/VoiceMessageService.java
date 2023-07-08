package com.override.recognizer_service.service;

import com.override.dto.AudioRecognizerGoRequestDTO;
import com.override.dto.VoiceMessageDTO;
import com.override.recognizer_service.service.voice.VoiceDTORecognitionService;
import com.override.recognizer_service.service.voice.VoiceDTORecognitionServiceImplGoAudioRecognizer;
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
        AudioRecognizerGoRequestDTO requestDTO = mapVoiceMessageDTOtoAudioRecognizerGoRequestDTO(voiceMessage);
        String textMessage = voiceDTORecognitionService.voiceToText(requestDTO);
        return wordsToNumbersService.wordsToNumbers(textMessage);
    }

    private AudioRecognizerGoRequestDTO mapVoiceMessageDTOtoAudioRecognizerGoRequestDTO(VoiceMessageDTO voiceMessage) {
        UUID id = UUID.randomUUID();
        return AudioRecognizerGoRequestDTO.builder()
                .id(id)
                .voiceMessage(voiceMessage.getVoiceMessageBytes())
                .build();
    }
}
