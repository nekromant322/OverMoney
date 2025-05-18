package com.overmoney.telegram_bot_service.kafka.service;

import com.override.dto.AccountDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@Slf4j
public class KafkaSubscriptionProducerService {

    @Autowired
    private KafkaTemplate<String, AccountDataDTO> kafkaTemplate;

    private final Map<Long, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    @Value("${spring.kafka.topics.subscription-request}")
    private String requestTopic;

    public CompletableFuture<String> send(AccountDataDTO dto) {
        CompletableFuture<String> future = new CompletableFuture<>();
        futures.put(dto.getUserId(), future);

        kafkaTemplate.send(requestTopic, dto.getUserId().toString(), dto)
                .addCallback(
                        result -> log.info("Sent subscription request: {}", dto),
                        ex -> {
                            log.error("Failed to send subscription request", ex);
                            future.completeExceptionally(ex);
                        }
                );

        return future;
    }

    public void complete(Long userId, String message) {
        CompletableFuture<String> future = futures.remove(userId);
        if (future != null) {
            future.complete(message);
        }
    }
}
