package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.exception.TelegramAuthException;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.service.TelegramVerificationService;
import com.override.orchestrator_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    TelegramVerificationService telegramVerificationService;

    @Autowired
    UserService userService;

    @PostMapping("/telegram")
    @ResponseStatus(HttpStatus.OK)
    public void authenticateUserFromTelegram(@RequestBody TelegramAuthRequest telegramAuthRequest) {
        if (telegramVerificationService.verify(telegramAuthRequest)) {
            log.info("User from Telegram authentication verified");
            userService.saveUser(telegramAuthRequest);
        } else {
            log.error("Telegram authentication failed for user "
                    + telegramAuthRequest.getUsername()
                    + ": encoded data does not match with the hash");
            throw new TelegramAuthException("Encoded data does not match with the hash");
        }
    }
}
