package com.override.payment_service.controller.rest;

import com.override.dto.AccountDataDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class SubscriptionController {

    //Todo
    @PostMapping("/check")
    public String checkSubscription(AccountDataDTO accountDataDTO) {
        return "Дата истечений подписки";
    }
}
