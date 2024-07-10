package com.override.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AmountRangeDTO {
    private int begin;
    private int end;

    @JsonCreator
    public AmountRangeDTO(@JsonProperty("begin") int begin, @JsonProperty("end") int end) {
        this.begin = begin;
        this.end = end;
    }
}


