package com.override.orchestrator_service.service;

import com.override.orchestrator_service.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private FileUtils fileUtils;

    public Optional<String> getImageBase64Format(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        try {
            BufferedImage image = fileUtils.downloadImage(url);
            if (image == null) {
                return Optional.empty();
            }
            image = fileUtils.resizeImage(image, 32, 32);
            String imageType = fileUtils.getImageFormatFromUrl(url);
            return Optional.of(String.format("data:image/%s;base64,%s",
                    imageType,
                    fileUtils.convertImageToBase64(image, imageType)));
        } catch (IOException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                return Optional.empty();
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error downloading image");
        }
    }
}
