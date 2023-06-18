package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.OverMoneyBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MergeAccountService {
    @Autowired
    private OverMoneyBot overMoneyBot;

    public void sendMergeRequest(Long userId) {
        overMoneyBot.sendMergeRequest(userId);
    }
}