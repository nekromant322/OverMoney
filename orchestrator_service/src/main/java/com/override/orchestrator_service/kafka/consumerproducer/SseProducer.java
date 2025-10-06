package com.override.orchestrator_service.kafka.consumerproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SseProducer {
    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Value("${spring.kafka.topics.sse}")
    private String requestTopic;

    public void sendMessage(Long id) {
        kafkaTemplate.send(requestTopic, id)
                .addCallback(
                        result -> log.info("Есть новая транзакция у: {}", id),
                        ex -> log.error("Ошибка отправки: {}", ex.getMessage())
                );
    }
}
