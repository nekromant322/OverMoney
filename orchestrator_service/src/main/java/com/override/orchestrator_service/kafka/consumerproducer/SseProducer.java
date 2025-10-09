package com.override.orchestrator_service.kafka.consumerproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
public class SseProducer {
    @Autowired
    private KafkaTemplate<String, UUID> kafkaTemplate;

    @Value("${spring.kafka.topics.sse}")
    private String requestTopic;

    public void sendMessage(UUID id) {
        kafkaTemplate.send(requestTopic, id)
                .addCallback(
                        result -> log.info("New transaction by: {}", id),
                        ex -> log.error("Error send: {}", ex.getMessage())
                );
    }
}
