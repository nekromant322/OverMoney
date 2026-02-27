package com.override.orchestrator_service.mapper;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.model.BugReport;
import org.springframework.stereotype.Component;

@Component
public class BugReportMapper {

    public BugReport mapDTOtoBugReport(BugReportDTO bugReportDTO) {
        return BugReport.builder()
                .report(bugReportDTO.getReport())
                .localDateTime(bugReportDTO.getLocalDateTime())
                .userId(bugReportDTO.getUserId())
                .build();
    }

    public static BugReportDTO mapBugReportToDTO(BugReport bugReport) {
        return BugReportDTO.builder()
                .id(bugReport.getId())
                .report(bugReport.getReport())
                .localDateTime(bugReport.getLocalDateTime())
                .userId(bugReport.getUserId())
                .build();
    }
}