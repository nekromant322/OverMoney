package com.override.orchestrator_service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class AnnounceDTO {

    private String announceText;
    private Set<Long> userIds;
}
