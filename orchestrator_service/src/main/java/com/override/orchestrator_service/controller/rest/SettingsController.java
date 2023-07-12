package com.override.orchestrator_service.controller.rest;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.service.BackupUserDataService;
import com.override.orchestrator_service.service.BugReportService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/settings")
@Slf4j
public class SettingsController {

    @Autowired
    private BackupUserDataService backupUserDataService;

    @Autowired
    private TelegramUtils telegramUtils;

    @Autowired
    private BugReportService bugReportService;

    @GetMapping("/backup")
    public BackupUserDataDTO getUserBackupData(Principal principal) throws InstanceNotFoundException {
        return backupUserDataService.createBackupUserData(telegramUtils.getTelegramId(principal));
    }

    @PostMapping("/bugreport")
    public ResponseEntity<HttpStatus> saveBugReport(@RequestBody BugReportDTO bugReportDTO, Principal principal){
        Long userId = telegramUtils.getTelegramId(principal);
        bugReportService.saveBugReport(bugReportDTO, userId);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
