package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.OverMoneyBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AnnounceService {

    @Autowired
    private OverMoneyBot overMoneyBot;

    public void sendAnnounce(String announceText, Set<Long> userIds) {
        for (Long id : userIds) {
            overMoneyBot.sendMessage(id, announceText);
        }
    }
}
