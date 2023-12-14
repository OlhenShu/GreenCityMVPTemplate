package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Test
    void searchEverythingTest() {
        // Arrange
        String searchQuery = "Title";
        Locale locale = Locale.getDefault();
        when(searchService.search(eq(searchQuery), eq(locale.getLanguage())))
                .thenReturn(SearchResponseDto.create(Collections.emptyList(), 0L));

        // Act
        ResponseEntity<SearchResponseDto> response = searchController.search(searchQuery, locale);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(searchService, times(1)).search(eq(searchQuery), eq(locale.getLanguage()));
    }

    @Test
    void searchEcoNewsTest() {
        // Arrange
        String searchQuery = "Eco news title";
        Locale locale = Locale.getDefault();
        when(searchService.searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage())))
                .thenReturn(new PageableDto<>(Collections.emptyList(), 0, 1, 1));

        // Act
        ResponseEntity<PageableDto<SearchNewsDto>> response = searchController.searchEcoNews(Pageable.unpaged(), searchQuery, locale);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(searchService, times(1)).searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage()));
    }
}