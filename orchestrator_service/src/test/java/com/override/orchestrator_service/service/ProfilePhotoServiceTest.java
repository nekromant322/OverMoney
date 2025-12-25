package com.override.orchestrator_service.service;

import com.override.orchestrator_service.feign.TelegramBotFeign;
import com.override.orchestrator_service.model.ProfilePhoto;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.repository.UserRepository;
import com.override.orchestrator_service.util.ImageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePhotoServiceTest {

    @InjectMocks
    private ProfilePhotoService profilePhotoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TelegramBotFeign telegramBotFeign;

    @Mock
    private ImageUtils imageUtils;

    @Test
    void saveProfilePhoto_shouldSetNewPhoto_whenNoProfilePhotoExists() {
        Long userId = 1L;
        byte[] photo = new byte[]{1, 2, 3};
        User user = new User();
        user.setId(userId);
        user.setProfilePhoto(null);

        when(telegramBotFeign.getTelegramPhoto(userId)).thenReturn(photo);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        profilePhotoService.saveProfilePhoto(userId);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getProfilePhoto() != null &&
                        Arrays.equals(photo, savedUser.getProfilePhoto().getPhotoData())
        ));
    }

    @Test
    void saveProfilePhoto_shouldRemovePhoto_whenPhotoDataIsNull() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setProfilePhoto(ProfilePhoto.builder().photoData(new byte[]{1, 2, 3}).build());

        when(telegramBotFeign.getTelegramPhoto(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        profilePhotoService.saveProfilePhoto(userId);

        verify(userRepository).save(argThat(savedUser -> savedUser.getProfilePhoto() == null));
    }

    @Test
    void saveProfilePhoto_shouldUpdatePhoto_whenProfilePhotoExists() {
        Long userId = 1L;
        byte[] newPhoto = new byte[]{9, 8, 7};
        ProfilePhoto existingPhoto = new ProfilePhoto();
        existingPhoto.setPhotoData(new byte[]{1, 2, 3});
        User user = new User();
        user.setId(userId);
        user.setProfilePhoto(existingPhoto);

        when(telegramBotFeign.getTelegramPhoto(userId)).thenReturn(newPhoto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        profilePhotoService.saveProfilePhoto(userId);

        verify(userRepository).save(argThat(savedUser ->
                Arrays.equals(savedUser.getProfilePhoto().getPhotoData(), newPhoto)
        ));
    }

    @Test
    void getProfilePhotoBase64_shouldReturnBase64String() throws Exception {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        when(imageUtils.resizeImage(any(), eq(32), eq(32))).thenReturn(testImage);
        when(imageUtils.convertImageToBase64(testImage, "png")).thenReturn("base64encoded");

        Optional<String> result = profilePhotoService.getProfilePhotoBase64(imageBytes);

        assertTrue(result.isPresent());
        assertEquals("data:image/png;base64,base64encoded", result.get());
    }

    @Test
    void getProfilePhotoBase64_shouldReturnEmpty_whenImageIsNull() {
        Optional<String> result = profilePhotoService.getProfilePhotoBase64(null);
        assertTrue(result.isEmpty());
    }
}
