package com.hallyugo.hallyugo.stamp.controller;

import com.hallyugo.hallyugo.auth.annotation.AuthUser;
import com.hallyugo.hallyugo.stamp.domain.response.StampResponseDto;
import com.hallyugo.hallyugo.stamp.service.StampService;
import com.hallyugo.hallyugo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class StampController {

    private final StampService stampService;

    @GetMapping("/stamp")
    public ResponseEntity<StampResponseDto> getUserStamp(
            @AuthUser User user,
            @RequestParam(name = "limit", defaultValue = "9") int limit
    ) {
        StampResponseDto result = stampService.getStampsByUser(user, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stamp/location")
    public ResponseEntity<StampResponseDto> getLocationStamp(
            @AuthUser User user,
            @RequestParam(name = "location_id") Long locationId
    ) {
        StampResponseDto result = stampService.getStampsByUserAndLocation(user, locationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/stamp/location", params = "location_id")
    public ResponseEntity<Void> createLocationStamp(
            @AuthUser User user,
            @RequestParam(name = "location_id") Long locationId
    ) {
        try {
            stampService.addLocationStamp(user, locationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}