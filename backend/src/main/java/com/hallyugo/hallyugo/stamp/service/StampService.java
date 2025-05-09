package com.hallyugo.hallyugo.stamp.service;

import com.hallyugo.hallyugo.common.exception.EntityNotFoundException;
import com.hallyugo.hallyugo.common.exception.ExceptionCode;
import com.hallyugo.hallyugo.location.domain.Location;
import com.hallyugo.hallyugo.location.repository.LocationRepository;
import com.hallyugo.hallyugo.stamp.domain.Stamp;
import com.hallyugo.hallyugo.stamp.domain.response.StampResponseDto;
import com.hallyugo.hallyugo.stamp.domain.response.StampResponseItem;
import com.hallyugo.hallyugo.stamp.repository.StampRepository;
import com.hallyugo.hallyugo.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StampService {

    private final StampRepository stampRepository;
    private final LocationRepository locationRepository;

    public StampResponseDto getStampsByUser(User user, int limit) {
        List<Stamp> stamps = stampRepository.findByUserId(user.getId());

        List<Stamp> limitedStamps = stamps.size() > limit ? stamps.subList(0, limit) : stamps;

        List<StampResponseItem> stampResponseItems = limitedStamps.stream()
                .map(StampResponseItem::toDto)
                .toList();

        StampResponseDto result = new StampResponseDto();
        result.setTotal(stamps.size());
        result.setStamps(stampResponseItems);
        return result;
    }

    public StampResponseDto getStampsByUserAndLocation(User user, Long locationId) {
        List<Stamp> stamps = stampRepository.findByUserIdAndLocationId(user.getId(), locationId);

        List<StampResponseItem> stampResponseItems = stamps.stream()
                .map(StampResponseItem::toDto).toList();

        return StampResponseDto.toDto(stampResponseItems);
    }

    @Transactional
    public void addLocationStamp(User user, Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException(
                ExceptionCode.ENTITY_NOT_FOUND));

        Stamp stamp = new Stamp(user, location);
        stampRepository.save(stamp);
    }
}