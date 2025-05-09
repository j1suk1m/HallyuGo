package com.hallyugo.hallyugo.content.controller;

import com.hallyugo.hallyugo.auth.AuthUserArgumentResolver;
import com.hallyugo.hallyugo.content.domain.Category;
import com.hallyugo.hallyugo.content.domain.Content;
import com.hallyugo.hallyugo.content.domain.response.ContentResponseDto;
import com.hallyugo.hallyugo.content.service.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser("user") // to avoid 401 error
@WebMvcTest(controllers = ContentController.class)
class ContentControllerTest {
    private static final String BASE_URL = "/api/v1/content";
    private static final String INITIAL_CONTENTS_PATH = BASE_URL + "/initial";
    private static final String CATEGORY_QUERY_STRING = "category=";
    private static final String KEYWORD_QUERY_STRING = "keyword=";
    private static final int INITIAL_CONTENTS_SIZE_PER_CATEGORY = 2;
    private static final int TOTAL_CONTENTS_SIZE_PER_CATEGORY = 3;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @MockBean
    private ContentService contentService;

    @DisplayName("카테고리별 랜덤 콘텐츠 조회 시 정상 결과가 반환되어야 한다.")
    @Test
    void 카테고리별_랜덤_콘텐츠_조회_성공_테스트() throws Exception {
        // given
        Map<String, List<ContentResponseDto>> result = generateRandomMockContentsByCategory();
        when(contentService.getRandomContents()).thenReturn(result);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(INITIAL_CONTENTS_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.DRAMA.length()").value(INITIAL_CONTENTS_SIZE_PER_CATEGORY))
                .andExpect(jsonPath("$.K_POP.length()").value(INITIAL_CONTENTS_SIZE_PER_CATEGORY))
                .andExpect(jsonPath("$.MOVIE.length()").value(INITIAL_CONTENTS_SIZE_PER_CATEGORY))
                .andExpect(jsonPath("$.NOVEL.length()").value(INITIAL_CONTENTS_SIZE_PER_CATEGORY))
                .andDo(print());
    }

    @DisplayName("주어진 카테고리에 해당하는 전체 콘텐츠 조회 시 정상 결과가 반환되어야 한다.")
    @ParameterizedTest
    @EnumSource(Category.class)
    void 카테고리별_전체_콘텐츠_조회_성공_테스트(Category category) throws Exception {
        // given
        List<ContentResponseDto> result = generateTotalMockContentsByCategory(category);
        when(contentService.getContentsByCategory(category.name())).thenReturn(result);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "?" + CATEGORY_QUERY_STRING + category.name())
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(TOTAL_CONTENTS_SIZE_PER_CATEGORY))
                .andDo(print());
    }

    @DisplayName("제목에 키워드가 포함된 전체 콘텐츠 조회 시 정상 결과가 반환되어야 한다.")
    @Test
    void 키워드_검색_성공_테스트() throws Exception {
        // given
        final int size = 5;
        final String keyword = "matchingKeyword";
        List<ContentResponseDto> result = generateMockContentsContainingKeyword(keyword, size);
        when(contentService.getContentsByKeyword(keyword)).thenReturn(result);

        // when & then
        performGetRequestWithKeywordAndCheckResponse(keyword, size);
    }

    @DisplayName("제목에 키워드가 포함된 콘텐츠가 없는 경우 정상 결과가 반환되어야 한다.")
    @Test
    void 키워드_검색_결과_없음_테스트() throws Exception {
        // given
        final int size = 0;
        final String nonMatchingKeyword = "nonMatchingKeyword";
        List<ContentResponseDto> result = Collections.emptyList();
        when(contentService.getContentsByKeyword(nonMatchingKeyword)).thenReturn(result);

        // when & then
        performGetRequestWithKeywordAndCheckResponse(nonMatchingKeyword, size);
    }

    private Map<String, List<ContentResponseDto>> generateRandomMockContentsByCategory() {
        Map<String, List<ContentResponseDto>> result = new HashMap<>();
        List<Content> dramaContents = new ArrayList<>();
        List<Content> kpopContents = new ArrayList<>();
        List<Content> movieContents = new ArrayList<>();
        List<Content> novelContents = new ArrayList<>();
        final String hashtag = "#hashtag";

        dramaContents.add(new Content(Category.DRAMA, "dramaTitle1", "dramaDesc1", "dramaUrl1", hashtag));
        dramaContents.add(new Content(Category.DRAMA, "dramaTitle2", "dramaDesc2", "dramaUrl2", hashtag));
        kpopContents.add(new Content(Category.K_POP, "kpopTitle1", "kpopDesc1", "kpopUrl1", hashtag));
        kpopContents.add(new Content(Category.K_POP, "kpopTitle2", "kpopDesc2", "kpopUrl2", hashtag));
        movieContents.add(new Content(Category.MOVIE, "movieTitle1", "movieDesc1", "movieUrl1", hashtag));
        movieContents.add(new Content(Category.MOVIE, "movieTitle2", "movieDesc2", "movieUrl2", hashtag));
        novelContents.add(new Content(Category.NOVEL, "novelTitle1", "novelDesc1", "novelUrl1", hashtag));
        novelContents.add(new Content(Category.NOVEL, "novelTitle2", "novelDesc2", "novelUrl2", hashtag));

        List<ContentResponseDto> dramaContentDtos = dramaContents.stream().map(ContentResponseDto::toDto).toList();
        List<ContentResponseDto> kpopContentDtos = kpopContents.stream().map(ContentResponseDto::toDto).toList();
        List<ContentResponseDto> movieContentDtos = movieContents.stream().map(ContentResponseDto::toDto).toList();
        List<ContentResponseDto> novelContentDtos = novelContents.stream().map(ContentResponseDto::toDto).toList();

        result.put(Category.DRAMA.name(), dramaContentDtos);
        result.put(Category.K_POP.name(), kpopContentDtos);
        result.put(Category.MOVIE.name(), movieContentDtos);
        result.put(Category.NOVEL.name(), novelContentDtos);

        return result;
    }

    private List<ContentResponseDto> generateTotalMockContentsByCategory(Category category) {
        List<Content> contents = new ArrayList<>();

        for (int i = 0; i < TOTAL_CONTENTS_SIZE_PER_CATEGORY; i++) {
            contents.add(new Content(category, category.name() + "Title" + i, category.name() + "Desc" + i,
                    category.name() + "Url" + i, "#" + category.name() + i));

        }

        return contents.stream().map(ContentResponseDto::toDto).toList();
    }

    private List<ContentResponseDto> generateMockContentsContainingKeyword(String keyword, int size) {
        List<Content> contents = new ArrayList<>();
        List<Category> categories = Arrays.asList(Category.values());

        for (int i = 0; i < size; i++) {
            Collections.shuffle(categories);
            contents.add(new Content(categories.getFirst(), keyword + i, "desc" + i,
                    "url" + i, "#hashtag" + i));
        }

        return contents.stream().map(ContentResponseDto::toDto).toList();
    }

    private void performGetRequestWithKeywordAndCheckResponse(String keyword, int expectedSize) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "?" + KEYWORD_QUERY_STRING + keyword)
                        .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andDo(print());
    }
}
