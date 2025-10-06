package com.override.orchestrator_service.kafka.consumerproducer;


import com.override.orchestrator_service.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(topics = "${spring.kafka.topics.sse}")
public class SseTransactionListener {
    @Autowired
    private SseService sseService;

    @KafkaHandler
    public void processTransaction(Long telegramUserId){
        sseService.sendNewTransactionsToUser(telegramUserId);
    }
}
