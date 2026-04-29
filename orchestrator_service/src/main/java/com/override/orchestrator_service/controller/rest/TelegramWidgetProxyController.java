package com.override.orchestrator_service.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@RestController
public class TelegramWidgetProxyController {

    private static final String WIDGET_URL = "https://telegram.org/js/telegram-widget.js";
    private static final long CACHE_TTL_MS = 24 * 60 * 60 * 1000L;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private volatile String cachedScript = null;
    private volatile long cachedAt = 0;

    @GetMapping("/tg-widget/telegram-widget.js")
    public ResponseEntity<String> widget() {
        String script = getScript();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/javascript; charset=utf-8"))
                .body(script);
    }

    private synchronized String getScript() {
        if (cachedScript != null && System.currentTimeMillis() - cachedAt < CACHE_TTL_MS) {
            return cachedScript;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WIDGET_URL))
                    .header("Accept-Encoding", "identity")
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            cachedScript = applyReplacements(response.body());
            cachedAt = System.currentTimeMillis();
            log.info("Telegram widget JS fetched and cached");
        } catch (IOException | InterruptedException e) {
            if (cachedScript != null) {
                log.warn("Failed to refresh Telegram widget JS, serving stale cache", e);
                return cachedScript;
            }
            throw new RuntimeException("Failed to fetch Telegram widget JS", e);
        }
        return cachedScript;
    }

    private String applyReplacements(String script) {
        return script
                .replace("https://core.telegram.org", "https://overmoney.tech")
                .replace("//core.telegram.org", "//overmoney.tech")
                .replace("https://oauth.telegram.org", "https://overmoney.tech")
                .replace("//oauth.telegram.org", "//overmoney.tech")
                .replace("oauth.telegram.org", "overmoney.tech");
    }
}
