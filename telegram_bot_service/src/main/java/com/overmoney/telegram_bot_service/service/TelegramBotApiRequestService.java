package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.TelegramBotApiFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Comparator;
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

        if (photos == null || photos.isEmpty() || photos.get(0) == null || photos.get(0).isEmpty()) {
            return null;
        }
        List<PhotoSize> profilePhotos = photos.get(0);
        return profilePhotos.stream()
                .min(Comparator.comparingInt(photo -> photo.getHeight() * photo.getWidth()))
                .map(PhotoSize::getFileId)
                .orElse(null);
    }
}
