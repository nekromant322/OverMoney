package com.override.orchestrator_service.service;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.mapper.BugReportMapper;
import com.override.orchestrator_service.model.BugReport;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BugReportMapperTest {

    @InjectMocks
    private BugReportMapper bugReportMapper;

    @Test
    public void BugReportToDTOTest() {
        BugReportDTO bugReportDTO = TestFieldsUtil.generateTestBugReportDTO();
        BugReport bugReport = bugReportMapper.mapDTOtoBugReport(bugReportDTO);
        Assertions.assertEquals(bugReport.getReport(), bugReportDTO.getReport());
        Assertions.assertEquals(bugReport.getUserId(), bugReportDTO.getUserId());
        Assertions.assertEquals(bugReport.getLocalDateTime(), bugReportDTO.getLocalDateTime());
    }
}
