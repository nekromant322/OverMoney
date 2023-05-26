package com.override.orchestrator_service.util;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class TelegramUtils {

    public static Long getTelegramId(Principal principal) {
        return ((JwtAuthentication) principal).getTelegramId();
    }
}
