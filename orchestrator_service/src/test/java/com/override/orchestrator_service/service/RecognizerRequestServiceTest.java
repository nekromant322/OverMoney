package com.override.orchestrator_service.service;

import com.override.orchestrator_service.feign.RecognizerFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecognizerRequestServiceTest {

    @InjectMocks
    private RecognizerRequestService recognizerRequestService;

    @Mock
    private RecognizerFeign recognizerFeign;


    @Test
    public void sendVoiceMessageTest() {
        byte[] voiceMessage = {1, 2, 3};

        recognizerRequestService.sendVoiceMessage(voiceMessage);

        verify(recognizerFeign, times(1)).sendVoiceMessage(voiceMessage);
    }
}
