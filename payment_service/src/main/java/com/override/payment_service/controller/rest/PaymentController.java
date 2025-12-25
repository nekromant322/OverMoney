package com.override.payment_service.controller.rest;

import com.override.dto.SubscriptionDTO;
import com.override.payment_service.model.Subscription;
import com.override.payment_service.service.RoboKassaInterface;
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

    private final RoboKassaInterface robokassaService;
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
        return robokassaService.updatePaymentStatus(allParams);
    }

    @GetMapping("/pay/{chatId}")
    @ResponseBody
    public ResponseEntity<String> getPaymentUrl(@PathVariable Long chatId) {
        return robokassaService.createPayment(chatId);
    }

    @GetMapping("/subscription/{chatId}/status")
    public SubscriptionDTO getSubscriptionByChatId(@PathVariable Long chatId) {
        try {
            Subscription subscription = subscriptionService.findByChatId(chatId).get(0);
            subscriptionService.checkActiveStatus(subscription);
            return SubscriptionDTO.builder()
                    .isActive(subscription.isActive())
                    .endDate(subscription.getEndDate())
                    .build();
        } catch (RuntimeException e) {
            return SubscriptionDTO.builder()
                    .isActive(false)
                    .endDate(null)
                    .build();
        }
    }
}