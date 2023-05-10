package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.TelegramBotApiFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramBotApiRequestService {

    @Autowired
    TelegramBotApiFeign telegramBotApiFeign;

    public byte[] getVoiceMessageBytes(String fileId) {
        return telegramBotApiFeign.getVoiceMessage(getTelegramFileUrl(fileId)).getBody();
    }

    private String getTelegramFileUrl(String fileId) {
        return telegramBotApiFeign.getTelegramFileData(fileId).getBody().getResult().getFilePath();
    }
}
