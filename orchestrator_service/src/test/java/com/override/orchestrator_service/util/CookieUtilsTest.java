package com.override.orchestrator_service.util;


import com.override.orchestrator_service.model.JwtResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

    private final CookieUtils cookieUtils = new CookieUtils();

    @Spy
    private HttpServletResponse mockResponse;

    @Test
    public void testAddAccessTokenCookie() {
        String TEST_ACCESS_TOKEN = "accessTokenTestValue";
        String TEST_REFRESH_TOKEN = "refreshTokenTestValue";
        JwtResponse token = new JwtResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);

        cookieUtils.addAccessTokenCookie(token, mockResponse);

        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(mockResponse, times(1)).addCookie(argument.capture());
        assertEquals(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, argument.getValue().getName());
        assertEquals(TEST_ACCESS_TOKEN, argument.getValue().getValue());
        assertTrue(argument.getValue().getSecure());
        assertTrue(argument.getValue().isHttpOnly());
        assertEquals(-1, argument.getValue().getMaxAge());
        assertEquals("/", argument.getValue().getPath());
    }

    @Test
    public void testRemoveAccessTokenCookie() {
        cookieUtils.removeAccessTokenCookie(mockResponse);

        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(mockResponse, times(1)).addCookie(argument.capture());
        assertEquals(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, argument.getValue().getName());
        assertNull(argument.getValue().getValue());
        assertTrue(argument.getValue().getSecure());
        assertTrue(argument.getValue().isHttpOnly());
        assertEquals(0, argument.getValue().getMaxAge());
        assertEquals("/", argument.getValue().getPath());
    }

    @Test
    public void testHasAccessTokenCookieWhenCookieExists() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{
                new Cookie("otherCookie", "value"),
                new Cookie(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, "tokenValue")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        assertTrue(cookieUtils.hasAccessTokenCookie(mockRequest));
    }

    @Test
    public void testHasAccessTokenCookieWhenCookieDoesNotExist() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{
                new Cookie("otherCookie1", "value1"),
                new Cookie("otherCookie2", "value2")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        assertFalse(cookieUtils.hasAccessTokenCookie(mockRequest));
    }

    @Test
    public void testHasAccessTokenCookieWhenNoCookies() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getCookies()).thenReturn(null);

        assertFalse(cookieUtils.hasAccessTokenCookie(mockRequest));
    }
}