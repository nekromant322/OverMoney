package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.TelegramBotApiFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.File;

import static com.overmoney.telegram_bot_service.utils.TestFieldsUtil.generateTelegramFile;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotApiRequestServiceTest {

    @InjectMocks
    private TelegramBotApiRequestService telegramBotApiRequestService;

    @Mock
    private TelegramBotApiFeign telegramBotApiFeign;


    @Test
    public void getVoiceMessageBytesTest() {
        String fileId = "12345";
        String filePath = "path";
        ResponseEntity<ApiResponse<File>> apiResponseResponseEntity = mock(ResponseEntity.class);
        ResponseEntity<byte[]> byteArrayResponseEntity = mock(ResponseEntity.class);
        ApiResponse<File> apiResponse = mock(ApiResponse.class);
        File file = generateTelegramFile();

        when(telegramBotApiFeign.getTelegramFileData(fileId)).thenReturn(apiResponseResponseEntity);
        when(apiResponseResponseEntity.getBody()).thenReturn(apiResponse);
        when(apiResponse.getResult()).thenReturn(file);
        when(telegramBotApiFeign.getVoiceMessage(filePath)).thenReturn(byteArrayResponseEntity);
        telegramBotApiRequestService.getVoiceMessageBytes(fileId);

        verify(telegramBotApiFeign, times(1)).getVoiceMessage(filePath);
        verify(telegramBotApiFeign, times(1)).getTelegramFileData(fileId);
    }
}
