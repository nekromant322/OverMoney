package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.model.AnnounceDTO;
import com.overmoney.telegram_bot_service.service.AnnounceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnounceController {

    @Autowired
    private AnnounceService announceService;

    @PostMapping("/announce")
    public void sendAnnounce(@RequestBody AnnounceDTO announceDTO) {
        announceService.sendAnnounce(announceDTO.getAnnounceText(), announceDTO.getUserIds());
    }
}
