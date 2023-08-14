package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugReportDTO {
    private Long id;
    private String report;
    private Long userId;
    private LocalDateTime localDateTime;
    private String username;
}
