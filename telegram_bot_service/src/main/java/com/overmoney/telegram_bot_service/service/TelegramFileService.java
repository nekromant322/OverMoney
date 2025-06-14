package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class TelegramFileService {
    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private TelegramBotApiRequestService telegramBotApiRequestService;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.api.url}")
    private String botApiUrl;

    public String getUserProfilePhotoBase64Format(Long userId) throws IOException {
        String fileId = telegramBotApiRequestService.getPhotoId(userId);
        if (fileId == null) {
            return null;
        }
        String filePath = telegramBotApiRequestService.getTelegramFileUrl(fileId);
        filePath = String.format("%s/file/bot%s/%s", botApiUrl, botToken, filePath);
        BufferedImage image = fileUtils.downloadImage(filePath);
        String imageType = fileUtils.getImageFormatFromUrl(filePath);
        if (image == null || imageType == null) {
            return null;
        }
        return String.format("data:image/%s;base64,%s", imageType, fileUtils.convertImageToBase64(image, imageType));
    }
}
