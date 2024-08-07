package com.override.orchestrator_service.controller.rest;


import com.override.dto.BackupUserDataDTO;
import com.override.orchestrator_service.annotations.OnlyServiceUse;
import com.override.orchestrator_service.service.BackupUserDataService;
import com.override.orchestrator_service.service.ExportUserDataService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
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
    private ExportUserDataService exportUserDataService;

    @GetMapping("/backup")
    public BackupUserDataDTO getUserBackupData(Principal principal) throws InstanceNotFoundException {
        return backupUserDataService.createBackupUserData(telegramUtils.getTelegramId(principal));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> getUserBackupDataExcel(Principal principal)
            throws IOException, InstanceNotFoundException {
        return exportUserDataService.downloadExelFile(telegramUtils.getTelegramId(principal));
    }

    @OnlyServiceUse
    @GetMapping("/backup/{id}")
    public BackupUserDataDTO getBackupDataFromRemoveUser(@PathVariable Long id) {
        return backupUserDataService.createBackupRemovedUserData(id);
    }

    @PostMapping("/backup/read")
    public ResponseEntity<HttpStatus> readBackupFile(@RequestBody BackupUserDataDTO backupUserDataDTO,
                                                     Principal principal) {
        backupUserDataService.writingDataFromBackupFile(backupUserDataDTO, telegramUtils.getTelegramId(principal));
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
