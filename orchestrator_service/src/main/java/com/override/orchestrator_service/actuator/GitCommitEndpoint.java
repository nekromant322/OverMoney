package com.override.orchestrator_service.actuator;

import com.override.orchestrator_service.actuator.config.InfoGitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "gitInfo")
@RequiredArgsConstructor
public class GitCommitEndpoint {

    @Autowired
    private ApplicationStartUpListener applicationStartUpListener;

    @Autowired
    private InfoGitProperties gitProperties;

    @ReadOperation
    public ActuatorGitInfoDTO getInfoDTO() {
        ActuatorGitInfoDTO dto = new ActuatorGitInfoDTO();

        dto.setApplicationStartUpTime(applicationStartUpListener.getStartUpTime());
        dto.setCommit(gitProperties.getCommit().getId());
        dto.setBranch(gitProperties.getBranch());
        dto.setUsername(gitProperties.getCommit().getUser().getName());
        dto.setCommitTime(gitProperties.getCommit().getTime());

        return dto;
    }
}