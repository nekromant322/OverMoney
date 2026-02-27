package com.override.orchestrator_service.service;

import com.override.dto.BugReportDTO;
import com.override.orchestrator_service.mapper.BugReportMapper;
import com.override.orchestrator_service.model.BugReport;
import com.override.orchestrator_service.repository.BugReportRepository;
import com.override.orchestrator_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BugReportService {
    @Autowired
    private BugReportRepository bugReportRepository;

    @Autowired
    private BugReportMapper bugReportMapper;

    @Autowired
    private UserRepository userRepository;

    public void saveBugReport(BugReportDTO bugReportDTO, Long userid) {
        bugReportDTO.setUserId(userid);
        bugReportRepository.save(bugReportMapper.mapDTOtoBugReport(bugReportDTO));
    }

    public List<BugReportDTO> getBugReportDTOList() {
        return bugReportRepository.findAllOrderedByIdDesc().stream()
                .map(BugReportMapper::mapBugReportToDTO)
                .peek(x -> x.setUsername(userRepository.findById(x.getUserId()).get().getUsername()))
                .collect(Collectors.toList());
    }

    public void deleteBugReport(Long id) {
        bugReportRepository.deleteById(id);
    }

    public BugReportDTO getBugReportDTO(Long id) {
        return BugReportMapper.mapBugReportToDTO(getBugReport(id));
    }

    public BugReport getBugReport(Long id) {
        return bugReportRepository.findById(id).get();
    }
}