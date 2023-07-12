package com.override.orchestrator_service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bug_reports")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BugReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report")
    private String report;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private LocalDateTime localDateTime;
}
