package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.overmoney.telegram_bot_service.model.OnboardingMessage;
import com.override.dto.OnboardingMessageDTO;
import com.override.dto.UserRegistrationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyncService {

    @Autowired
    private OrchestratorFeign orchestratorFeign;

    @Autowired
    private UserService userService;

    @Autowired
    private UserOnboardingMessageService userOnboardingMessageService;

    @Autowired
    private OnboardingMessageService onboardingMessageService;

    public void syncUsers() {
        Set<Long> existingUserIds = userService.getAllUsersIds();
        List<UserRegistrationInfoDto> newUsers = orchestratorFeign.getUserRegistrationInfo(existingUserIds);
        userService.saveUsers(newUsers);
        log.info("Синхронизация пользователей завершена. Синхронизировано {} пользователей", newUsers.size());
    }

    @Transactional
    public void syncOnboardingMessages(List<OnboardingMessageDTO> messages) {
        Set<UUID> incomingIds = messages.stream()
                .map(OnboardingMessageDTO::getMessageId)
                .collect(Collectors.toSet());

        Set<UUID> existingIds = onboardingMessageService.findAllIds();

        Set<UUID> toDelete = existingIds
                .stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        if (!toDelete.isEmpty()) {
            onboardingMessageService.deleteAllById(toDelete);
            userOnboardingMessageService.deleteAllByOnboardingMessageIdIn(toDelete);
        }

        List<OnboardingMessage> toSave = new ArrayList<>();

        for (OnboardingMessageDTO dto : messages) {
            Optional<OnboardingMessage> existingOpt = onboardingMessageService.findById(dto.getMessageId());

            OnboardingMessage onboardingMessage;
            if (existingOpt.isEmpty()) {
                onboardingMessage = OnboardingMessage.builder()
                        .id(dto.getMessageId())
                        .message(dto.getMessage())
                        .dayDelay(dto.getDayOffset())
                        .image(dto.getImage())
                        .build();
                toSave.add(onboardingMessage);
            }
        }
        onboardingMessageService.saveAll(toSave);
    }
}
