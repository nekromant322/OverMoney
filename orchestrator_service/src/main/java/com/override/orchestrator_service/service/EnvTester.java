package com.override.orchestrator_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class EnvTester {
    @Value("${spring.datasource.url}")
    String dbUrl;

    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;

    @PostConstruct
    public void test() {
        log.error("dbUrl " + dbUrl);
        log.error("username " + username);
        log.error("password " + password);
    }
}
