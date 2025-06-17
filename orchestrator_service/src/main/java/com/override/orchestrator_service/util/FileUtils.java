package com.override.orchestrator_service.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@Component
public class FileUtils {

    public String convertImageToBase64(BufferedImage image, String imageType) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageType, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
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

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}