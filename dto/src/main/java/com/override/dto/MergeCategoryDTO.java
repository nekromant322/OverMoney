package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeCategoryDTO {
    private Long categoryToChangeId;
    private Long categoryToMergeId;
}
