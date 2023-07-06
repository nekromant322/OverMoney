package com.override.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BackupUserDataDTO {
    private List<TransactionDTO> transactionDTOList;
    private List<CategoryDTO> categoryDTOList;
}
