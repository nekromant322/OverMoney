package com.override.payment_service.controller.rest;

import com.override.dto.SubscriptionStatusDTO;
import com.override.payment_service.model.PaymentCallback;
import com.override.payment_service.service.PayingService;
import com.override.payment_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PayingService payingService;
    private final SubscriptionService subscriptionService;

    /**
     * Колбэк для успешной оплаты
     */
    @GetMapping("/success")
    public String successCallback() {
        return "redirect::/settings";
    }

    /**
     * Колбэк для неуспешной оплаты
     */
    @GetMapping("/fail")
    public String failCallback(
            @RequestParam("OutSum") BigDecimal amount,
            @RequestParam("InvId") Long invoiceId) {

        log.warn("Неуспешная оплата: invoiceId={}, amount={}", invoiceId, amount);

        return "OK";
    }

    /**
     * Колбэк для уведомления об оплате (server-to-server)
     */
    @PostMapping(value = "/result")
    public ResponseEntity<String> resultCallback(@RequestParam Map<String, String> allParams) {
        return ResponseEntity.ok(payingService.handlePaymentCallback(new PaymentCallback(allParams)));
    }

    @GetMapping("/pay/{chatId}")
    public String getPaymentUrl(@PathVariable Long chatId) {
        return payingService.createPayment(chatId);
    }

    //TODO обновление на сервере статуса (активно или нет) по расписанию
    @GetMapping("/subscription/{chatId}/status")
    public SubscriptionStatusDTO getSubscriptionByChatId(@PathVariable Long chatId) {
        return subscriptionService.getSubscriptionStatus(chatId);
    }
}