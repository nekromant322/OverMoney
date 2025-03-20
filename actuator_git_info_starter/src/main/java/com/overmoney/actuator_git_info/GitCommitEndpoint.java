package com.overmoney.actuator_git_info;

import com.overmoney.actuator_git_info.config.InfoGitProperties;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "gitInfo")
@RequiredArgsConstructor
@AllArgsConstructor
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