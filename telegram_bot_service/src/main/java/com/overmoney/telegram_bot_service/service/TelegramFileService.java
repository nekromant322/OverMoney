package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

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

    public Optional<byte[]> getUserProfilePhoto(Long userId) {
        Optional<String> fileIdOpt = Optional.ofNullable(telegramBotApiRequestService.getPhotoId(userId));
        if (fileIdOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            String fileId = fileIdOpt.get();
            String filePath = telegramBotApiRequestService.getTelegramFileUrl(fileId);
            filePath = String.format("%s/file/bot%s/%s", botApiUrl, botToken, filePath);
            BufferedImage image = fileUtils.downloadImage(filePath);
            String imageType = fileUtils.getImageFormatFromUrl(filePath);
            if (image == null || imageType == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(fileUtils.convertImageToByteArray(image, imageType));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при попытке загрузки файла");
        }
    }
}