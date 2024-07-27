package com.override.orchestrator_service.config;

import com.override.orchestrator_service.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
public class CleanSchedulerConfiguration {

    @Autowired
    private KeywordService keywordService;

    @Scheduled(fixedRateString = "#{${clean.interval} * 24 * 60 * 60 * 1000}")
    @Transactional
    public void scheduleClean() {
        keywordService.cleanDepricatedKeywords();
    }
}
