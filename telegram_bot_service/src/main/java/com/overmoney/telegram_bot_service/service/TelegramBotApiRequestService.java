package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.TelegramBotApiFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import java.util.List;

@Service
public class TelegramBotApiRequestService {

    @Autowired
    private TelegramBotApiFeign telegramBotApiFeign;



    public byte[] getVoiceMessageBytes(String fileId) {
        return telegramBotApiFeign.getVoiceMessage(getTelegramFileUrl(fileId)).getBody();
    }

    public String getTelegramFileUrl(String fileId) {
        return telegramBotApiFeign.getTelegramFileData(fileId).getBody().getResult().getFilePath();
    }

    public String getPhotoId(Long userId) {
        List<List<PhotoSize>> photos = telegramBotApiFeign
                .getUserProfilePhotos(userId)
                .getBody()
                .getResult()
                .getPhotos();

        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return photos
                .get(0)
                .get(0)
                .getFileId();
    }
}
