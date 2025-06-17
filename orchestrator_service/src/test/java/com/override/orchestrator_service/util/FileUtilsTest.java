package com.override.orchestrator_service.util;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {

    private final FileUtils fileUtils = new FileUtils();


    @Test
    void convertImageToBase64() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        String base64 = fileUtils.convertImageToBase64(image, "png");
        assertThat(base64).isNotNull();
    }

    @Test
    void getImageFormatFromUrl_shouldDetectCorrectFormat() {
        assertThat(fileUtils.getImageFormatFromUrl("https://photo.ru/image.png")).isEqualTo("png");
        assertThat(fileUtils.getImageFormatFromUrl("https://photo.ru/photo.JPG")).isEqualTo("jpg");
        assertThat(fileUtils.getImageFormatFromUrl("https://photo.ru/photo.jpeg")).isEqualTo("jpg");
        assertThat(fileUtils.getImageFormatFromUrl("https://photo.ru/file.gif")).isNull();
    }

    @Test
    void resizeImageTest() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage resizedImage = fileUtils.resizeImage(originalImage, 32, 32);
        assertThat(resizedImage.getWidth()).isEqualTo(32);
        assertThat(resizedImage.getHeight()).isEqualTo(32);
    }
}
