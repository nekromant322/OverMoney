package com.override.orchestrator_service.controller.rest;

import com.override.dto.BugReportDTO;
import com.override.dto.MailDTO;
import com.override.dto.StatisticDTO;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.service.*;
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

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OverMoneyAccountService accountService;

    @Autowired
    private RecognizerFeign recognizerFeign;

    @Autowired
    private StatisticService statisticService;

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

    @GetMapping("/countTransactionsLastDays/{numberDays}")
    public int getTransactionsCountLastDays(@PathVariable("numberDays") int numberDays) {
        return transactionService.getTransactionsCountLastDays(numberDays);
    }

    @GetMapping("/activeAccountCount/{numberDays}")
    public int getActiveUsersCount(@PathVariable("numberDays") int numberDays) {
        return accountService.getActiveAccountCount(numberDays);
    }

    @GetMapping("/deepseek/balance")
    public String getDeepSeekBalance() {
        String balance = recognizerFeign.getBalance();
        return balance;
    }

    @GetMapping("/deepseek/stat")
    public StatisticDTO getSuggestionStatistic() {
        return statisticService.calculateStatistic();
    }
}
