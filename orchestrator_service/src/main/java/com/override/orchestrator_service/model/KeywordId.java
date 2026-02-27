package com.override.orchestrator_service.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class KeywordId implements Serializable {
    private String name;
    private Long accountId;
}
