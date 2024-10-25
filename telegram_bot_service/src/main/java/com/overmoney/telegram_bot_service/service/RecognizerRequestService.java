package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.exception.VoiceProcessingException;
import com.overmoney.telegram_bot_service.feign.RecognizerFeign;
import com.override.dto.VoiceMessageDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.overmoney.telegram_bot_service.constants.MessageConstants.NETWORK_ERROR;

@Service
@Slf4j
public class RecognizerRequestService {

    @Autowired
    private RecognizerFeign recognizerFeign;

    public String sendVoiceMessage(VoiceMessageDTO voiceMessage) {
        try {
            return recognizerFeign.sendVoiceMessage(voiceMessage);
        } catch (FeignException e) {
            log.error("Network error: " + e.status(), e);
            throw new VoiceProcessingException(NETWORK_ERROR, e);
        }
    }
}
