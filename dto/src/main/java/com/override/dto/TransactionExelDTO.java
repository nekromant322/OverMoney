package com.override.dto;


import com.override.dto.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionExelDTO {
    private Type type;
    private String category;
    private Number amount;
    private String message;
    private LocalDateTime date;
}
