package com.override.orchestrator_service.config.filter;

import org.springframework.beans.factory.annotation.Value;
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


    @Value("${filters.allowed-URIs}")
    private List<String> allowedURIList;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        return request.getCookies() != null || allowedURIList.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String internalKey = request.getHeader(HEADER_NAME);

        if ((internalKey != null) && internalKey.equals(secretKey)) {
            filterChain.doFilter(request, response);
        }
    }
}