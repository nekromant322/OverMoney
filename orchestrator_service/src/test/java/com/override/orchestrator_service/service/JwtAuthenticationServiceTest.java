package com.override.orchestrator_service.service;

import com.override.orchestrator_service.config.jwt.JwtProvider;
import com.override.orchestrator_service.exception.TelegramAuthException;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestAuthRequest;
import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationServiceTest {

    @InjectMocks
    private JwtAuthenticationService jwtAuthenticationService;

    @Mock
    private UserService userService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private TelegramVerificationService telegramVerificationService;

    @Test
    @SneakyThrows
    public void testLoginWithValidTelegramAuthData() {
        TelegramAuthRequest telegramAuthRequest = generateTestAuthRequest();
        User user = generateTestUser();

        when(telegramVerificationService.verify(telegramAuthRequest)).thenReturn(true);
        when(userService.getUserByUsername(telegramAuthRequest.getUsername())).thenReturn(user);
        jwtAuthenticationService.login(telegramAuthRequest);

        verify(userService, times(1)).saveUser(telegramAuthRequest);
        verify(userService, times(1)).getUserByUsername(telegramAuthRequest.getUsername());
        verify(jwtProvider, times(1)).generateAccessToken(user);
        verify(jwtProvider, times(1)).generateRefreshToken(user);
    }

    @Test
    @SneakyThrows
    public void testLoginWithInvalidTelegramAuthData() {
        TelegramAuthRequest telegramAuthRequest = generateTestAuthRequest();

        when(telegramVerificationService.verify(telegramAuthRequest)).thenReturn(false);

        assertThrows(TelegramAuthException.class, () -> jwtAuthenticationService.login(telegramAuthRequest));
    }
}
