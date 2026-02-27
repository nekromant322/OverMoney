package com.override.orchestrator_service.util;


import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramUtilsTest {

    @InjectMocks
    private TelegramUtils telegramUtils;

    @Test
    public void testGetTelegramId() {
        long telegramTestId = 123456789L;
        JwtAuthentication principal = new JwtAuthentication();
        principal.setTelegramId(telegramTestId);

        long result = telegramUtils.getTelegramId(principal);

        Assertions.assertEquals(telegramTestId, result);
    }
}