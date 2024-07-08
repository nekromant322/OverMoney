package com.overmoney.telegram_bot_service.controller.rest;


import com.overmoney.telegram_bot_service.commands.AnnounceCommand;
import com.override.dto.AnnounceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.bots.AbsSender;

@RestController
public class AnnounceController {

    @Autowired
    private AnnounceCommand announceCommand;
    @Autowired
    private AbsSender absSender;

    @PostMapping("/announce")
    public void sendAnnounce(@RequestBody AnnounceDTO announceDTO) {
        announceCommand.sendAnnounce(announceDTO.getAnnounceText(), announceDTO.getUserIds(), absSender);
    }
}
