package com.override.orchestrator_service.config.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class InternalKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("ZALUPA")
    private String secretKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        return request.getCookies() != null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String internalKey = request.getHeader("X-INTERNAL-KEY");

        if ((internalKey != null) && internalKey.equals(secretKey)) {
            filterChain.doFilter(request, response);
        }
    }
}