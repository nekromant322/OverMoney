package com.override.orchestrator_service.service;

import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.model.ProfilePhoto;
import com.override.orchestrator_service.repository.UserRepository;
import com.override.orchestrator_service.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Optional;

@Service
public class ProfilePhotoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramBotFeign telegramBotFeign;

    @Autowired
    private ImageUtils imageUtils;

    @Async
    public void saveProfilePhoto(Long id) {
        byte[] photoData = telegramBotFeign.getTelegramPhoto(id);

        userRepository.findById(id).ifPresent(user -> {
            ProfilePhoto profilePhoto = user.getProfilePhoto();

            if (photoData == null) {
                user.setProfilePhoto(null);
            } else if (profilePhoto == null) {
                user.setProfilePhoto(ProfilePhoto.builder()
                        .photoData(photoData)
                        .build());
            } else {
                profilePhoto.setPhotoData(photoData);
            }

            userRepository.save(user);
        });
    }

    public Optional<String> getProfilePhotoBase64(byte[] photoBytes) {
        if (photoBytes == null || photoBytes.length == 0) {
            return Optional.empty();
        }
        try (InputStream is = new ByteArrayInputStream(photoBytes)) {
            BufferedImage image = ImageIO.read(is);
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(photoBytes));
            if (image == null || contentType == null) {
                return Optional.empty();
            }
            BufferedImage resizedImage = imageUtils.resizeImage(image, 32, 32);
            String formatType = contentType.substring(contentType.indexOf('/') + 1);
            return Optional.of(String.format("data:%s;base64,%s",
                    contentType,
                    imageUtils.convertImageToBase64(resizedImage, formatType)));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing image bytes");
        }
    }
}
