package com.override.orchestrator_service.config.filter;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class InternalKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${filters.authorization-header.header-value}")
    private String secretKey;

    private final String HEADER_NAME = "X-INTERNAL-KEY";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String securityHeader = request.getHeader(HEADER_NAME);
        return securityHeader == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String internalKey = request.getHeader(HEADER_NAME);
        if ((internalKey != null) && internalKey.equals(secretKey)) {
            Authentication authentication = new JwtAuthentication();
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}