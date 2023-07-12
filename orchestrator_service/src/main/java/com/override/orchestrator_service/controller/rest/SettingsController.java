package com.override.orchestrator_service.controller.rest;


import com.fasterxml.jackson.core.JsonToken;
import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.service.BackupUserDataService;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONTokener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/backup/read")
    public ResponseEntity<HttpStatus> readBackupFile(@RequestParam BackupUserDataDTO backupUserDataDTO) {


        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
