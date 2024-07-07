package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class DateRangeDTO {
    private LocalDateTime start;
    private LocalDateTime end;

    public DateRangeDTO() {
    }

    @JsonCreator
    public DateRangeDTO(@JsonProperty("start") LocalDateTime start, @JsonProperty("end") LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
}