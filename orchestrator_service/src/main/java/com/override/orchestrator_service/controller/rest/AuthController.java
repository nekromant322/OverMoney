package com.override.orchestrator_service.controller.rest;

import com.override.dto.AuthorizationTokenDTO;
import com.override.orchestrator_service.model.JwtResponse;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.service.JwtAuthenticationService;
import com.override.orchestrator_service.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    private CookieUtils cookieUtils;

    @PostMapping("/login")
    public AuthorizationTokenDTO login(@RequestBody TelegramAuthRequest telegramAuthRequest,
                                       final HttpServletResponse response)
            throws NoSuchAlgorithmException, InvalidKeyException, InstanceNotFoundException {
        final JwtResponse token = jwtAuthenticationService.login(telegramAuthRequest);
        cookieUtils.addAccessTokenCookie(token, response);

        return new AuthorizationTokenDTO(token.getAccessToken());
    }
}