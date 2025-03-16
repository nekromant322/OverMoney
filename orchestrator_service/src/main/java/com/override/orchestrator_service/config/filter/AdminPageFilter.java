package com.override.orchestrator_service.config.filter;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import lombok.extern.slf4j.Slf4j;
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

    private final String ADMIN_PATH = "/admin";
    private final String SWAGGER_PATH = "/swagger";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getUsername();
        for (String allowedUsername : allowedUsers) {
            if (username.equals(allowedUsername)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith(ADMIN_PATH) && !path.startsWith(SWAGGER_PATH);
    }
}
