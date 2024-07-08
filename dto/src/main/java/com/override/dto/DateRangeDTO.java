package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class DateRangeDTO {
    private LocalDateTime begin;
    private LocalDateTime end;

    @JsonCreator
    public DateRangeDTO(@JsonProperty("begin") LocalDateTime begin, @JsonProperty("end") LocalDateTime end) {
        this.begin = begin;
        this.end = end;
    }
}