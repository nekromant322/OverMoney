package com.override.orchestrator_service.config;

import com.override.orchestrator_service.repository.KeywordRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class CleanSchedulerConfiguration {

    @Autowired
    private KeywordRepository keywordRepository;

    @Value("${clean-deprecated-keywords.max-days}")
    private int maxDays;

    @Value("${clean-deprecated-keywords.min-usage}")
    private int minUsageCount;

    @Scheduled(fixedRateString = "#{${clean-deprecated-keywords.interval} * 24 * 60 * 60 * 1000}")
    @SchedulerLock(name = "cleanKeyword", lockAtLeastFor = "10m", lockAtMostFor = "15m")
    @Transactional
    public void scheduleClean() {
        LocalDateTime maxDate = LocalDateTime.now().minusDays(maxDays);
        keywordRepository.deleteDepricatedKeywords(maxDate, minUsageCount);
    }
}
