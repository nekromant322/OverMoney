package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AmountRangeDTO {
    private int start;
    private int end;

    @JsonCreator
    public AmountRangeDTO(@JsonProperty("start") int start, @JsonProperty("end") int end) {
        this.start = start;
        this.end = end;
    }
}


