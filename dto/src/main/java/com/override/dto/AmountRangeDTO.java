package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Диапазон цен")
public class AmountRangeDTO {

    @Schema(description = "минимальное значение", example = "35", nullable = true)
    private int begin;

    @Schema(description = "максимальное значение", example = "9999", nullable = true)
    private int end;

    @JsonCreator
    public AmountRangeDTO(@JsonProperty("begin") int begin, @JsonProperty("end") int end) {
        this.begin = begin;
        this.end = end;
    }
}


