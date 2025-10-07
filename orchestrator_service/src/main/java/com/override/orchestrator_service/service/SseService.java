package com.override.orchestrator_service.service;


import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public void addSubscription(User user, FluxSink<ServerSentEvent> fluxSink) {
        fluxSink.onCancel(() -> {
                    subscriptions.remove(user.getId());
                    log.info("subscription " + user.getUsername() + " was closed");
                }
        );
        subscriptions.put(user.getId(), fluxSink);
        log.info("Под {} добавил подписку для {}", instanceId, user.getUsername());
    }

    public void sendInitData(Long id, FluxSink<ServerSentEvent> fluxSink) {
        try {
            List<TransactionDTO> transactionsDTO = transactionService.findTransactionsListByUserIdWithoutCategories(id).stream()
                    .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                    .collect(Collectors.toList());
            ServerSentEvent<List<TransactionDTO>> event = ServerSentEvent.builder(transactionsDTO).build();
            fluxSink.next(event);
        } catch (InstanceNotFoundException e) {
            log.error("Error sending init data to user {}: {}", id, e.getMessage());
        }
    }

    public void sendNewTransactionsToUser(Long id) {
        if (subscriptions.containsKey(id)) {
            sendInitData(id, subscriptions.get(id));
        }
    }
}
