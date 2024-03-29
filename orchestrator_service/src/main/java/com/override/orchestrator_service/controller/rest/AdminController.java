package com.override.orchestrator_service.controller.rest;

import com.override.dto.BugReportDTO;
import com.override.dto.MailDTO;
import com.override.orchestrator_service.service.BugReportService;
import com.override.orchestrator_service.service.TelegramBotRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TelegramBotRequestService telegramBotRequestService;

    @Autowired
    private BugReportService bugReportService;

    @PostMapping("/announce")
    public void sendAnnounce(@RequestBody String text) {
        telegramBotRequestService.sendAnnounce(text);
    }

    @GetMapping("/mail/status")
    public List<MailDTO> getStatus() {
        return telegramBotRequestService.getStatusOfMails();
    }

    @GetMapping("/bugreports")
    public List<BugReportDTO> bugReportDTOList() {
        return bugReportService.getBugReportDTOList();
    }

    @DeleteMapping("/bugreports/{id}")
    public void deleteBugReport(@PathVariable Long id) {
        bugReportService.deleteBugReport(id);
    }

    @GetMapping("/bugreports/{id}")
    public BugReportDTO getBugReport(@PathVariable Long id) {
        return bugReportService.getBugReportDTO(id);
    }
}
