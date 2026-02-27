package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.feign.PaymentFeign;
import com.override.orchestrator_service.mapper.HttpResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentFeign paymentFeign;
    private final HttpResponseMapper httpResponseMapper;

    @PostMapping(value = "/result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> callBackResult(
            HttpServletRequest request) throws IOException {
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (!StringUtils.hasText(body)) {
            return ResponseEntity.badRequest().body("Robokassa прислала пустые параметры");
        }
        return paymentFeign.resultCallback(httpResponseMapper.toMap(body));
    }

    @GetMapping("/pay/{chatId}")
    public ResponseEntity<String> getPaymentUrl(@PathVariable Long chatId) {
        return paymentFeign.getPaymentUrl(chatId);
    }

    @GetMapping("/subscription/{chatId}/status")
    public String getSubscriptionByChatId(@PathVariable Long chatId) {
        return paymentFeign.getSubscriptionByChatId(chatId);
    }
}
