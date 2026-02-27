package com.overmoney.telegram_bot_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@Slf4j
public class FileUtils {

    public byte[] convertImageToByteArray(BufferedImage image, String imageType) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageType, baos);
            return baos.toByteArray();
        }
    }

    public BufferedImage downloadImage(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        return ImageIO.read(url);
    }

    public String getImageFormatFromUrl(String imageUrl) {
        String lowerCaseImageUrl = imageUrl.toLowerCase();
        if (lowerCaseImageUrl.endsWith(".png")) {
            return "png";
        }
        if (lowerCaseImageUrl.endsWith(".jpg") || lowerCaseImageUrl.endsWith(".jpeg")) {
            return "jpg";
        }
        return null;
    }

    public byte[] loadImageFromResources(String imageName) {
        try {
            ClassPathResource imgFile = new ClassPathResource("images/" + imageName);
            try (InputStream is = imgFile.getInputStream()) {
                return is.readAllBytes();
            }
        } catch (IOException e) {
            log.error("При установке фокти из ресурсова возникла ошибка: {}", e.getMessage());
            return null;
        }
    }
}