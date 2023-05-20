package com.override.orchestrator_service.config.filter;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.config.jwt.JwtFilter;
import com.override.orchestrator_service.config.jwt.JwtProvider;
import com.override.orchestrator_service.config.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final String ADMIN_PANEL_PATH = "/admin/path";
    private final String SEND_ANNOUNCE_PATH = "/announce/send";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = jwtFilter.getTokenFromRequest(request);
        final Claims claims = jwtProvider.getAccessClaims(token);
        final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
        final String username = claims.getSubject();
        for (String allowedUsername : allowedUsers) {
            if (username.equals(allowedUsername)) {
                jwtInfoToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.equals(ADMIN_PANEL_PATH) && !path.equals(SEND_ANNOUNCE_PATH);
    }
}
