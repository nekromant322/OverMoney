package com.overmoney.telegram_bot_service.kafka.event;

import com.override.dto.TransactionResponseDTO;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@KafkaListener(topics = "transaction-response-events-topic")
public class KafkaConsumerTransactionResponse {

    private CompletableFuture<TransactionResponseDTO> future;

    @KafkaHandler
    public void receiveTransaction(TransactionResponseDTO transaction) {
        if (future != null) {
            future.complete(transaction);
            future = null;
        }
    }

    public CompletableFuture<TransactionResponseDTO> getFuture() {
        future = new CompletableFuture<>();
        return future;
    }
}
