package com.override.orchestrator_service.service;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.mapper.BugReportMapper;
import com.override.orchestrator_service.model.BugReport;
import com.override.orchestrator_service.repository.BugReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BugReportServiceTest {

    @Mock
    private BugReportRepository bugReportRepository;

    @Mock
    private BugReportMapper bugReportMapper;

    @InjectMocks
    private BugReportService bugReportService;

    @Test
    public void saveBugReportTest() {
        BugReportDTO bugReportDTO = new BugReportDTO();
        BugReport bugReport = new BugReport();
        Long userId = 1L;

        when(bugReportMapper.mapDTOtoBugReport(bugReportDTO)).thenReturn(bugReport);
        when(bugReportRepository.save(bugReport)).thenReturn(bugReport);

        bugReportService.saveBugReport(bugReportDTO, userId);

        verify(bugReportRepository, times(1)).save(bugReport);
    }
}
