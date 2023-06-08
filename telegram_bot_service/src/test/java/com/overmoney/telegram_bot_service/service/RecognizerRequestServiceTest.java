package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.RecognizerFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecognizerRequestServiceTest {
    @InjectMocks
    private RecognizerRequestService recognizerRequestService;

    @Mock
    private RecognizerFeign recognizerFeign;

    @Test
    public void sendVoiceMessageTest() {
        byte[] voiceMessageByteArray = {1, 2, 3};

        recognizerRequestService.sendVoiceMessage(voiceMessageByteArray);

        verify(recognizerFeign, times(1)).sendVoiceMessage(voiceMessageByteArray);
    }
}
