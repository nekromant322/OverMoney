package com.override.orchestrator_service.service;

import com.override.dto.AnnounceDTO;
import com.override.dto.MailDTO;
import com.override.orchestrator_service.feign.TelegramBotFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotRequestService {

    @Autowired
    private TelegramBotFeign telegramBotFeign;
    @Autowired
    private UserService userService;

    public void sendAnnounce(String announceText) {
        telegramBotFeign.sendAnnounce(AnnounceDTO.builder()
                        .announceText(announceText.replaceAll("\"", ""))
                        .userIds(userService.getAllUserIds())
                        .build());
    }

    public List<MailDTO> getStatusOfMails() {
        return telegramBotFeign.getStatusOfMails();
    }
}
