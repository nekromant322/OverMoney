package com.overmoney.telegram_bot_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.override.dto.BackupUserDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class FileService {
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;

    public String createBackupFileToRemoteInChatUser(Long chatId, Long userId) {
        BackupUserDataDTO backupUserDataDTO = orchestratorRequestService.getBackup(chatId);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        String nameFile = userId.toString() + "-" + LocalDate.now() + "-overmoney.json";
        try {
            mapper.writeValue(new File(nameFile), backupUserDataDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameFile;
    }
}
