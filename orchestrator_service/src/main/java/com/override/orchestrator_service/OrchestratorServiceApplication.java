package com.override.orchestrator_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrchestratorServiceApplication {

    public static void main(String[] args) {
        System.out.println("host");
        System.out.println(System.getenv("ORCHESTRATOR_DB_HOST"));
        System.out.println("port");
        System.out.println(System.getenv("ORCHESTRATOR_DB_PORT"));
        System.out.println("user");
        System.out.println(System.getenv("ORCHESTRATOR_DB_USER"));
        System.out.println("password");
        System.out.println(System.getenv("ORCHESTRATOR_DB_PASSWORD"));
        SpringApplication.run(OrchestratorServiceApplication.class, args);
    }
}