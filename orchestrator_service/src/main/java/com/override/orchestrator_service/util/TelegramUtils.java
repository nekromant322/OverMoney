package com.override.orchestrator_service.util;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@Getter
public class TelegramUtils {

    @Value("${TELEGRAM_BOT_NAME}")
    private String telegramBotName;

    public Long getTelegramId(Principal principal) {
        return ((JwtAuthentication) principal).getTelegramId();
    }
}
