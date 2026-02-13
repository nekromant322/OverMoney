package com.override.payment_service.controller.rest;

import com.override.dto.SubscriptionDTO;
import com.override.payment_service.service.RoboKassaService;
import com.override.payment_service.service.SubscriptionService;
import com.override.payment_service.util.HttpParametr;
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

    private final RoboKassaService robokassaService;
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
        return robokassaService.updatePaymentStatus(new HttpParametr(allParams));
    }

    @GetMapping("/pay/{chatId}")
    public String getPaymentUrl(@PathVariable Long chatId) {
        return robokassaService.createPayment(chatId);
    }

    @GetMapping("/subscription/{chatId}/status")
    public SubscriptionDTO getSubscriptionByChatId(@PathVariable Long chatId) {
        return subscriptionService.getSubscriptionWithStatusByChatId(chatId);
    }
}