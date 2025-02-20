package com.override.orchestrator_service.feign;


import com.override.dto.AnnounceDTO;
import com.override.dto.MailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "telegram-bot", url = "${integration.internal.host.telegram-bot}")
public interface TelegramBotFeign {

    @PostMapping("/announce")
    void sendAnnounce(AnnounceDTO announceDTO);

    @PostMapping("/merge")
    void sendMergeRequest(@RequestParam Long userId);

    @GetMapping("/mail/status")
    List<MailDTO> getStatusOfMails();

    @DeleteMapping("/telegram-message/{id}")
    void deleteTelegramMessageById(@PathVariable("id") UUID id);

    @DeleteMapping("/telegram-message/ids")
    void deleteTelegramMessageByIds(@RequestParam List<UUID> ids);
}
