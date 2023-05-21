package com.override.orchestrator_service.config.jwt;

import com.override.orchestrator_service.model.Role;
import io.jsonwebtoken.Claims;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setTelegramId(Long.parseLong(claims.getSubject()));
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.get("username", String.class));
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        final List<Role> roles = claims.get("roles", List.class);
        return new HashSet<>(roles);
    }
}
