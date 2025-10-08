package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.config.jwt.JwtAuthentication;
import com.override.orchestrator_service.kafka.consumerproducer.SseProducer;
import com.override.orchestrator_service.service.SseService;
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

@Slf4j
@RestController
@RequestMapping("/sse")
@Tag(name = "Контроллер SSE соединений", description = "Операции, связанные с Server Sent Events соединениями")
public class SseController {
    @Autowired
    private SseService sseService;
    @Autowired
    private SseProducer sseProducer;

    @GetMapping(value = "/open-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> openStream(Authentication authentication) {
        if (authentication == null && !(authentication.getPrincipal() instanceof JwtAuthentication)) {
            return Flux.error(new RuntimeException("Not authenticated"));
        }
        JwtAuthentication jwt = (JwtAuthentication) authentication;
        return sseService.createSseStream(jwt.getTelegramId());
    }

    @PostMapping("/uncategorized-notify")
    public ResponseEntity<Void> handleUncategorizedTransaction(@RequestParam UUID transactionId) {
        sseProducer.sendMessage(transactionId);
        return ResponseEntity.ok().build();
    }
}
