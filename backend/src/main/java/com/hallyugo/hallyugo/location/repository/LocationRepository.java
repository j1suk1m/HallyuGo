package com.hallyugo.hallyugo.location.repository;

import com.hallyugo.hallyugo.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByContentId(Long contentId);

    List<Location> findByTitleContainingIgnoreCase(String keyword);
}