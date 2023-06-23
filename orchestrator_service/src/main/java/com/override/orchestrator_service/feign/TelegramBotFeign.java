package com.override.orchestrator_service.feign;


import com.override.dto.AnnounceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="telegram-bot-service")
public interface TelegramBotFeign {

    @PostMapping("/announce")
    void sendAnnounce(AnnounceDTO announceDTO);

    @PostMapping("/merge")
    void sendMergeRequest(@RequestParam Long userId);
}
