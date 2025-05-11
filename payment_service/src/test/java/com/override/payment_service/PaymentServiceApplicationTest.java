package com.override.payment_service;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
 class PaymentServiceApplicationTest {

 @MockBean
 private SpringLiquibase liquibase;

 @MockBean
 private KafkaTemplate<String, String> kafkaTemplate;

 @Test
 void contextLoads() {
 }

 @Test
 void main_shouldRunWithoutExceptions() {
  PaymentServiceApplication.main(new String[]{});
 }
}
