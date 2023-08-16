package com.override.dto.tinkoff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinkoffInfoDTO {
    private Long tinkoffAccountId;
    private String token;
    private Long favoriteAccountId;
}
