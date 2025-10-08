package com.override.orchestrator_service.service;


import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionMapper transactionMapper;

    @Value("${HOSTNAME:local}")
    private String instanceId;

    private Map<Long, FluxSink<ServerSentEvent>> subscriptions = new ConcurrentHashMap<>();

    public Flux<ServerSentEvent> createSseStream(Long userId) {
        Flux<ServerSentEvent> dataStream = Flux.create(fluxSink -> {
            try {
                log.info("Create subscription for " + userId);
                addSubscription(userId, fluxSink);
            } catch (Exception e) {
                log.error("Error creating SSE for {}", userId, e);
                fluxSink.error(e);
            }
        });
        Flux<ServerSentEvent> hertbeatStream = Flux.interval(Duration.ofSeconds(5))
                .map(tick -> ServerSentEvent.builder()
                        .event("ping")
                        .data("{\"timestamp\":\"" + Instant.now() + "\"}")
                        .build());
        return Flux.merge(dataStream, hertbeatStream);
    }

    private void addSubscription(Long userId, FluxSink<ServerSentEvent> fluxSink) {
        subscriptions.put(userId, fluxSink);
        log.info("Pod {} add subscription for {}", instanceId, userId);
    }

    public void sendNewTransactionToUser(UUID transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Long userId = transaction.getTelegramUserId();
        if (subscriptions.containsKey(userId)) {
            TransactionDTO transactionDTO = transactionMapper.mapTransactionToDTO(transaction);
            FluxSink<ServerSentEvent> fluxSink = subscriptions.get(userId);
            ServerSentEvent<TransactionDTO> event = ServerSentEvent
                    .builder(transactionDTO)
                    .event("transactions")
                    .build();
            fluxSink.next(event);
            log.info("Send message, {} in subscription", userId);
        } else {
            log.info("Do not send message, {} not in subscription", userId);
        }
    }
}
