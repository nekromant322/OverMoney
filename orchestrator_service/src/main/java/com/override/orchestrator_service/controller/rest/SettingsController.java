package com.override.orchestrator_service.controller.rest;


import com.override.dto.BackupUserDataDTO;
import com.override.orchestrator_service.service.BackupUserDataService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/backup")
    public BackupUserDataDTO getUserBackupData(Principal principal) throws InstanceNotFoundException {
        return backupUserDataService.createBackupUserData(telegramUtils.getTelegramId(principal));
    }
}
