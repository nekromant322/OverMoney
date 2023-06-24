package com.override.orchestrator_service.config.jwt;

import io.jsonwebtoken.Claims;

public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setTelegramId(Long.parseLong(claims.getSubject()));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.get("username", String.class));
        return jwtInfoToken;
    }
}
