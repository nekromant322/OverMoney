package com.overmoney.actuator_git_info;

import lombok.Getter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.time.LocalDateTime;

@Getter
public class ApplicationStartUpListener implements ApplicationListener<ApplicationReadyEvent> {

    LocalDateTime startUpTime;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        startUpTime = LocalDateTime.now();
    }
}