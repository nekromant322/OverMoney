package com.override.orchestrator_service.config.filter;

import com.override.orchestrator_service.config.jwt.JwtFilter;
import com.override.orchestrator_service.config.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AdminPageFilter extends OncePerRequestFilter {

    @Value("${admin.allowed_users}")
    private String[] allowedUsers;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private JwtProvider jwtProvider;
    private final String ADMIN_PATH = "/admin";
    private final String USERNAME_CLAIM = "username";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = jwtFilter.getTokenFromRequest(request);
        final Claims claims = jwtProvider.getAccessClaims(token);
        final String username = claims.get(USERNAME_CLAIM, String.class);
        for (String allowedUsername : allowedUsers) {
            if (username.equals(allowedUsername)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith(ADMIN_PATH);
    }
}
