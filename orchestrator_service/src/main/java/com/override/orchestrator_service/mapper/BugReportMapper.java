package com.override.orchestrator_service.mapper;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.model.BugReport;
import org.springframework.stereotype.Component;

@Component
public class BugReportMapper {

    public BugReport mapDTOtoBugReport(BugReportDTO bugReportDTO){
        return BugReport.builder()
                .report(bugReportDTO.getReport())
                .localDateTime(bugReportDTO.getLocalDateTime())
                .userId(bugReportDTO.getUserId())
                .build();
    }
}
