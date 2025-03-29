package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Schema(description = "Диапазон дат")
public class DateRangeDTO {

    @Schema(description = "Начальная дата", example = "2024-01-01T00:00:00", nullable = true)
    private LocalDateTime begin;

    @Schema(description = "Конечная дата", example = "2024-12-31T23:59:59", nullable = true)
    private LocalDateTime end;

    @JsonCreator
    public DateRangeDTO(@JsonProperty("begin") LocalDateTime begin, @JsonProperty("end") LocalDateTime end) {
        this.begin = begin;
        this.end = end;
    }
}