package com.override.orchestrator_service.actuator;

import lombok.Getter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//дать кондитианал только есть включен актуатор
@Component
@Getter
public class ApplicationStartUpListener implements ApplicationListener<ApplicationReadyEvent> {

    LocalDateTime startUpTime;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        startUpTime = LocalDateTime.now();
    }
}