package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.ModelUtils.getUserVO;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.SearchService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {
    private final String searchLink = "/search";
    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    private Locale locale = Locale.ENGLISH;

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    void searchEverythingTest() {
        String searchQuery = "Title";
        Locale locale = Locale.getDefault();
        when(searchService.search(eq(searchQuery), eq(locale.getLanguage())))
                .thenReturn(new SearchResponseDto(Collections.emptyList(), Collections.emptyList(), 0L, 0L));

        ResponseEntity<SearchResponseDto> response = searchController.search(searchQuery, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(searchService, times(1)).search(eq(searchQuery), eq(locale.getLanguage()));
    }

    @Test
    void searchEcoNewsTest() {
        String searchQuery = "Eco news title";
        Locale locale = Locale.getDefault();
        when(searchService.searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage())))
                .thenReturn(new PageableDto<>(Collections.emptyList(), 0, 1, 1));

        ResponseEntity<PageableDto<SearchNewsDto>> response = searchController.searchEcoNews(Pageable.unpaged(), searchQuery, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(searchService, times(1)).searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage()));
    }

    @Test
    void searchEventsTest() throws Exception {
        String searchQuery = "Eco news title";
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc
            .perform(
                get(searchLink + "/events")
                    .param("searchQuery",searchQuery)
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(pageSize))
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(searchService).searchAllEvents(pageable, searchQuery, locale.getLanguage());
    }
}