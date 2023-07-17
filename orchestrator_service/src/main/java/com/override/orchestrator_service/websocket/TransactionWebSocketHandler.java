package com.override.orchestrator_service.websocket;

import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TransactionWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionMapper transactionMapper;

    private final Logger log = LoggerFactory.getLogger(TransactionWebSocketHandler.class);
    Map<Long, WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
//        if (!webSocketSessionMap.containsKey(telegramUtils.getTelegramId(session.getPrincipal()))) {
//            webSocketSessionMap.put(telegramUtils.getTelegramId(session.getPrincipal()), session);
//        }
        webSocketSessionMap.put(telegramUtils.getTelegramId(session.getPrincipal()), session);
        log.info("--> Открыта сессия: " + Objects.requireNonNull(session.getPrincipal()).getName() + " " + session);
        try {
            session.sendMessage(sendRecentlyAddTransactionToFront(session.getPrincipal()));
        } catch (IOException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        try {
            webSocketSessionMap.remove(telegramUtils.getTelegramId(session.getPrincipal()));
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        webSocketSessionMap.forEach((key, value) -> {
            try {
//                log.info("-> Открыта сессия: " + Objects.requireNonNull(value.getPrincipal()).getName() + " " + value);
                log.info("Map<K,V>: <" + Objects.requireNonNull(value.getPrincipal()).getName() + ", " + value + ">");
                value.sendMessage(sendRecentlyAddTransactionToFront(value.getPrincipal()));
            } catch (IOException | InstanceNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private TextMessage sendRecentlyAddTransactionToFront(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions = transactionService
                .findTransactionsListByUserIdWithoutCategories(telegramUtils.getTelegramId(principal));
        transactions.sort(Comparator.comparing(Transaction::getDate));

        return new TextMessage(transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList()).toString()); //TODO Сюда надо передать транзакцию
    }
}