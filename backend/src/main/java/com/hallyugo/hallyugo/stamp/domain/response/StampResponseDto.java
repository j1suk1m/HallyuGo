package com.hallyugo.hallyugo.stamp.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StampResponseDto {

    @JsonProperty("total")
    private int total;

    @JsonProperty("stamps")
    private List<StampResponseItem> stamps;

    private StampResponseDto(List<StampResponseItem> stamps) {
        this.total = stamps.size();
        this.stamps = stamps;
    }

    public static StampResponseDto toDto(List<StampResponseItem> stamps) {
        return new StampResponseDto(stamps);
    }
}