package com.overmoney.telegram_bot_service.controller.rest;

import com.overmoney.telegram_bot_service.service.MailService;
import com.override.dto.MailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/status")
    public List<MailDTO> getStatusOfMails() {
        return mailService.getCountOfMailsStatusByAnnounceId();
    }
}

