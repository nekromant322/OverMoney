package com.overmoney.telegram_bot_service.scheduler;

import com.overmoney.telegram_bot_service.config.OnboardingProperties;
import com.overmoney.telegram_bot_service.service.OnboardingMessageDispatchService;
import com.overmoney.telegram_bot_service.service.SyncService;
import com.overmoney.telegram_bot_service.util.FileUtils;
import com.override.dto.OnboardingMessageDTO;
import com.override.dto.OnboardingStepDTO;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OnboardingMessageScheduler {
    @Autowired
    private OnboardingMessageDispatchService onboardingMessageDispatchService;

    @Autowired
    private OnboardingProperties onboardingProperties;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private SyncService syncService;

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "onboardingMessageScheduler", lockAtMostFor = "10m", lockAtLeastFor = "2m")
    public void onboardingSchedule() {
        syncService.syncUsers();
        List<OnboardingMessageDTO> messages = buildOnboardingMessagesList();
        syncService.syncOnboardingMessages(messages);
        onboardingMessageDispatchService.checkAndDispatch();
    }

    private List<OnboardingMessageDTO> buildOnboardingMessagesList() {
        List<OnboardingMessageDTO> messages = new ArrayList<>();

        for (OnboardingStepDTO step : onboardingProperties.getSteps()) {
            byte[] imageBytes = fileUtils.loadImageFromResources(step.getImage());
            UUID messageId = generateOnboardingMessageId(step, imageBytes);

            messages.add(OnboardingMessageDTO.builder()
                    .messageId(messageId)
                    .message(step.getText())
                    .dayOffset(step.getDayOffset())
                    .image(imageBytes)
                    .build());
        }

        return messages;
    }

    private UUID generateOnboardingMessageId(OnboardingStepDTO step, byte[] imageBytes) {
        String key = step.getText() + step.getDayOffset();
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(keyBytes.length + imageBytes.length);
        buffer.put(keyBytes);
        buffer.put(imageBytes);
        return UUID.nameUUIDFromBytes(buffer.array());
    }
}
