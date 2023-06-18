package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.MergeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MergeController {
    @Autowired
    private MergeAccountService mergeAccountService;

    @PostMapping("/merge")
    public void sendMergeRequest(@RequestParam Long userId) {
        mergeAccountService.sendMergeRequest(userId);
    }
}