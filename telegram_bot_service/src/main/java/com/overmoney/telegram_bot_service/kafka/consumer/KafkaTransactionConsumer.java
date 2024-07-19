package com.overmoney.telegram_bot_service.kafka.consumer;

import com.overmoney.telegram_bot_service.kafka.service.KafkaProducerService;
import com.override.dto.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionConsumer {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "transaction-response-events-topic")
    public void receiveTransactionResponse(TransactionResponseDTO responseDTO) {
        Long chatId = responseDTO.getChatId();
        kafkaProducerService.completeResponse(chatId, responseDTO);
    }
}
