package com.override.orchestrator_service.service;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.config.jwt.JwtProvider;
import com.override.orchestrator_service.exception.TelegramAuthException;
import com.override.orchestrator_service.model.JwtResponse;
import com.override.orchestrator_service.model.TelegramAuthRequest;
import com.override.orchestrator_service.model.User;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import javax.naming.AuthenticationException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TelegramVerificationService telegramVerificationService;
    private final ProfilePhotoService profilePhotoService;

    public JwtResponse login(TelegramAuthRequest telegramAuthRequest)
            throws NoSuchAlgorithmException, InvalidKeyException, InstanceNotFoundException {
        if (telegramVerificationService.verify(telegramAuthRequest)) {

            userService.saveUser(telegramAuthRequest);
            final User user = userService.getUserById(telegramAuthRequest.getId());
            profilePhotoService.saveProfilePhoto(user.getId());
            return new JwtResponse(jwtProvider.generateAccessToken(user), jwtProvider.generateRefreshToken(user));
        } else {
            throw new TelegramAuthException(
                    "Telegram authentication failed for user " + telegramAuthRequest.getUsername() +
                            ": encoded data does not match with the hash");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken)
            throws AuthenticationException, InstanceNotFoundException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String id = claims.getSubject();
            final User user = userService.getUserById(Long.parseLong(id));
            if (Objects.isNull(user)) {
                throw new AuthenticationException("No user registered with this token");
            }
            final String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken, null);
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws AuthenticationException, InstanceNotFoundException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String id = claims.getSubject();
            final User user = userService.getUserById(Long.parseLong(id));
            if (Objects.isNull(user)) {
                throw new AuthenticationException("No user registered with this token");
            }
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, newRefreshToken);
        }
        throw new AuthenticationException("Invalid JWT token");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}