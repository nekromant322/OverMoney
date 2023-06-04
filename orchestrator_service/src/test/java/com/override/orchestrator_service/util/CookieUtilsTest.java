package com.override.orchestrator_service.util;


import com.override.orchestrator_service.model.JwtResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

    private CookieUtils cookieUtils = new CookieUtils();

    @Spy
    private HttpServletResponse mockResponse;

    @Test
    public void testAddAccessTokenCookie() {
        String TEST_ACCESS_TOKEN = "accessTokenTestValue";
        String TEST_REFRESH_TOKEN = "refreshTokenTestValue";
        JwtResponse token = new JwtResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        Cookie cookie = new Cookie("accessToken", TEST_ACCESS_TOKEN);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");


        cookieUtils.addAccessTokenCookie(token, mockResponse);

        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(mockResponse, times(1)).addCookie(argument.capture());
        assertEquals("accessToken", argument.getValue().getName());
        assertEquals(TEST_ACCESS_TOKEN, argument.getValue().getValue());
    }
}