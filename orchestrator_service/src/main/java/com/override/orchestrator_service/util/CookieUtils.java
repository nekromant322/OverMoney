package com.override.orchestrator_service.util;

import com.override.orchestrator_service.model.JwtResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {
    public void addAccessTokenCookie(JwtResponse token, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", token.getAccessToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
