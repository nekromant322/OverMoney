package com.overmoney.telegram_bot_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.File;

@FeignClient(name = "TelegramBotApiFeign", url = "${bot.api.url}")
public interface TelegramBotApiFeign {

    @GetMapping("/bot${bot.token}/getFile?file_id={fileId}")
    ResponseEntity<ApiResponse<File>> getTelegramFileData(@PathVariable String fileId);

    @GetMapping("/file/bot${bot.token}/{filePath}")
    ResponseEntity<byte[]> getVoiceMessage(@PathVariable String filePath);
}
