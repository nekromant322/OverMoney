package com.override.orchestrator_service.controller.rest;

import com.override.dto.AuthorizationTokenDTO;
import com.override.orchestrator_service.model.JwtResponse;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.service.JwtAuthenticationService;
import com.override.orchestrator_service.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtils.removeAccessTokenCookie(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuth(HttpServletRequest request) {
        return ResponseEntity.ok(cookieUtils.hasAccessTokenCookie(request));
    }
}