package com.override.payment_service.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface RoboKassaInterface {
    ResponseEntity<String> createPayment(Long chatId);

    ResponseEntity<String> updatePaymentStatus(Map<String, String> allParams);
}
