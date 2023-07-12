package com.override.orchestrator_service.service;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.mapper.BugReportMapper;
import com.override.orchestrator_service.repository.BugReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BugReportService {
    @Autowired
    private BugReportRepository bugReportRepository;

    @Autowired
    private BugReportMapper bugReportMapper;

    public void saveBugReport(BugReportDTO bugReportDTO, Long userid) {
        bugReportDTO.setUserId(userid);
        bugReportRepository.save(bugReportMapper.mapDTOtoBugReport(bugReportDTO));
    }
}
