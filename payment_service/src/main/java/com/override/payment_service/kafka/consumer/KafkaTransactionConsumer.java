package com.override.payment_service.kafka.consumer;

import com.override.dto.TransactionResponseDTO;
import com.override.payment_service.kafka.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
public class KafkaTransactionConsumer {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "${spring.kafka.topics.response}")
    public void receiveTransactionResponse(TransactionResponseDTO responseDTO) {
        kafkaProducerService.completeResponse(responseDTO);
    }
}
