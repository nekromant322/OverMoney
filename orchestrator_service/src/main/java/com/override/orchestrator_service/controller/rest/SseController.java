package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.kafka.consumerproducer.SseProducer;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.model.User;
import com.override.orchestrator_service.service.SseService;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/sse")
@Tag(name = "Контроллер SSE соединений", description = "Операции, связанные с Server Sent Events соединениями")
public class SseController {
    @Autowired
    private UserService userService;
    @Autowired
    private SseService sseService;
    @Autowired
    private SseProducer sseProducer;
    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/open-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> openStream(Authentication authentication) {
        if (authentication == null && !(authentication.getPrincipal() instanceof JwtAuthentication)) {
            return Flux.error(new RuntimeException("Not authenticated"));
        }
        String username = (String) authentication.getPrincipal();
        return Flux.create(fluxSink -> {
            try {
                User user = userService.getUserByUsername(username);
                log.info("create subscription for " + username);
                sseService.addSubscription(user, fluxSink);
                CompletableFuture.runAsync(()-> sseService.sendInitData(user.getId(), fluxSink));
            } catch (Exception e){
                log.error("Error creating SSE for {}", username, e);
                fluxSink.error(e);
            }
        });
    }

    @PostMapping("/uncategorized-notify")
    public ResponseEntity<Void> handleUncategorizedTransaction(@RequestParam UUID transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        sseProducer.sendMessage(transaction.getTelegramUserId());
        return ResponseEntity.ok().build();
    }
}
