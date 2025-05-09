package com.hallyugo.hallyugo.location.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hallyugo.hallyugo.image.domain.response.ImageResponseDto;
import com.hallyugo.hallyugo.location.domain.Location;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationWithImagesResponseDto {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String title;

    @JsonProperty
    private BigDecimal latitude;

    @JsonProperty
    private BigDecimal longitude;

    @JsonProperty
    private String description;

    @JsonProperty("video_link")
    private String videoLink;

    @JsonProperty("favorite_cnt")
    private Long favoriteCount;

    @JsonProperty
    private String pose;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty
    @Getter
    private List<ImageResponseDto> images = new ArrayList<>();

    private LocationWithImagesResponseDto(Location location, List<ImageResponseDto> images) {
        this.id = location.getId();
        this.title = location.getTitle();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.description = location.getDescription();
        this.videoLink = location.getVideoLink();
        this.favoriteCount = location.getFavoriteCount();
        this.pose = location.getPose();
        this.createdAt = location.getCreatedAt();
        this.updatedAt = location.getUpdatedAt();
        this.images = images;
    }

    public static LocationWithImagesResponseDto toDto(Location location, List<ImageResponseDto> images) {
        return new LocationWithImagesResponseDto(location, images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        LocationWithImagesResponseDto that = (LocationWithImagesResponseDto) obj;

        return Objects.equals(id, that.id);
    }

}
