package com.override.orchestrator_service.service;

import com.override.orchestrator_service.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private ImageService imageService;

    @Test
    void testGetImageBase64FormatWhenUrlIsNullOrBlank() {
        List<String> urls = Arrays.asList(null, "", " ", "    ");
        for (String url : urls) {
            Optional<String> result = imageService.getImageBase64Format(url);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testGetImageBase64FormatWhenImageIsNull() throws IOException {
        when(fileUtils.downloadImage("some_url")).thenReturn(null);
        Optional<String> result = imageService.getImageBase64Format("some_url");
        assertTrue(result.isEmpty());
    }


}
