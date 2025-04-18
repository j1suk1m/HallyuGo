package com.hallyugo.hallyugo.content.service;

import com.hallyugo.hallyugo.common.exception.EntityNotFoundException;
import com.hallyugo.hallyugo.common.exception.ExceptionCode;
import com.hallyugo.hallyugo.content.domain.Category;
import com.hallyugo.hallyugo.content.domain.Content;
import com.hallyugo.hallyugo.content.domain.response.ContentForMapResponseDto;
import com.hallyugo.hallyugo.content.domain.response.ContentResponseDto;
import com.hallyugo.hallyugo.content.repository.ContentRepository;
import com.hallyugo.hallyugo.location.domain.response.LocationWithImagesResponseDto;
import com.hallyugo.hallyugo.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContentService {
    private static final int PAGE_NUMBER = 0;
    private static final int INITIAL_CONTENTS_SIZE_PER_CATEGORY = 2;
    private final ContentRepository contentRepository;
    private final LocationService locationService;

    public Map<String, List<ContentResponseDto>> getRandomContents() {
        Map<String, List<ContentResponseDto>> result = new HashMap<>();
        Pageable pageable = PageRequest.of(PAGE_NUMBER, INITIAL_CONTENTS_SIZE_PER_CATEGORY);

        for (Category category : Category.values()) {
            List<ContentResponseDto> contentResponseDtos = getRandomContentsByCategory(category, pageable);
            result.put(category.name(), contentResponseDtos);
        }

        return result;
    }

    private List<ContentResponseDto> getRandomContentsByCategory(Category category, Pageable pageable) {
        List<Content> contents = contentRepository.findRandomContentsByCategory(category, pageable);
        return contents.stream().map(ContentResponseDto::toDto).toList();
    }

    public List<ContentResponseDto> getContentsByCategory(String category) {
        List<Content> contents = contentRepository.findByCategory(Category.valueOf(category));
        return contents.stream().map(ContentResponseDto::toDto).toList();
    }

    public List<ContentResponseDto> getContentsByKeyword(String keyword) {
        List<Content> contents = contentRepository.findByTitleContainingIgnoreCase(keyword);
        return contents.stream().map(ContentResponseDto::toDto).toList();
    }

    public ContentForMapResponseDto getContentWithLocationsAndImages(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionCode.ENTITY_NOT_FOUND));

        List<LocationWithImagesResponseDto> locationsWithImages =
                locationService.getLocationsWithImagesByContentId(contentId);

        return ContentForMapResponseDto.toDto(content, locationsWithImages);
    }

    public List<ContentForMapResponseDto> getContentsWithLocationsAndImagesByCategory(String category) {
        List<Content> contents = contentRepository.findByCategory(Category.valueOf(category));

        List<ContentForMapResponseDto> contentDtos = contents.stream()
                .map(content -> getContentWithLocationsAndImages(content.getId())).collect(Collectors.toList());

        return contentDtos;
    }

    public List<ContentForMapResponseDto> getContentsWithLocationsAndImagesByKeyword(String keyword) {
        List<Content> contents = contentRepository.findByTitleContainingIgnoreCase(keyword);

        return contents.stream().map(content -> {
            List<LocationWithImagesResponseDto> locationsWithImages =
                    locationService.getLocationsWithImagesByContentId(content.getId());
            return ContentForMapResponseDto.toDto(content, locationsWithImages);
        }).toList();
    }

}
