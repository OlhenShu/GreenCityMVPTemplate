package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.repository.EcoNewsSearchRepo;
import greencity.repository.EventSearchRepo;
import greencity.service.EcoNewsService;
import greencity.service.EventService;
import greencity.service.SearchServiceImpl;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Pageable;

import static greencity.ModelUtils.getSearchNewsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SearchServiceImplTest {
    @InjectMocks
    private SearchServiceImpl searchService;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private EventService eventService;

    private final String languageCode = "en";
    private final String title = "title";

    @Test
    void search() {
        List<SearchNewsDto> ecoNews = List.of(getSearchNewsDto());
        List<SearchEventDto> events = new ArrayList<>();
        events.add(SearchEventDto.builder()
                .id(1L)
                .organizer(new EventAuthorDto(1L,"test",1D))
                .title(title)
            .build());
        PageableDto<SearchNewsDto> ecoNewspageableDto = new PageableDto<>(
                ecoNews, 10L, 0, 1
        );
        PageableDto<SearchEventDto> eventDtoPageableDto = new PageableDto<>(
            events, 10L, 0,1
        );
        SearchResponseDto expected = new SearchResponseDto(ecoNews, events, 10L, 10L);

        when(ecoNewsService.search(anyString(),anyString())).thenReturn(ecoNewspageableDto);
        when(eventService.search(any(Pageable.class), anyString(),anyString())).thenReturn(eventDtoPageableDto);

        SearchResponseDto actual = searchService.search(title, languageCode);

        verify(ecoNewsService).search(title, languageCode);
        verify(eventService).search(PageRequest.of(0,3), title,languageCode);
        assertEquals(expected, actual);
    }

    @Test
    void searchAllNewsTest () {
        List<SearchNewsDto> eventList = new ArrayList<>();
        eventList.add(SearchNewsDto.builder()
            .id(1L)
            .title("title1")
            .author(EcoNewsAuthorDto.builder().id(1L).name("test").build())
            .creationDate(ZonedDateTime.now())
            .tags(ModelUtils.getTags().stream().map(Tag::toString).collect(Collectors.toList()))
            .build());
        eventList.add(SearchNewsDto.builder()
            .id(2L)
            .title("title2")
            .author(EcoNewsAuthorDto.builder().id(1L).name("test").build())
            .creationDate(ZonedDateTime.now())
            .tags(ModelUtils.getTags().stream().map(Tag::toString).collect(Collectors.toList()))
            .build());
        PageableDto<SearchNewsDto> searchEventDtoPageableDto = new PageableDto<>(eventList,eventList.size(),0,1);

        when(ecoNewsService.search(any(Pageable.class), anyString(), anyString())).thenReturn(searchEventDtoPageableDto);

        var response = searchService.searchAllNews(PageRequest.of(0,5),"Test", "ua");

        verify(ecoNewsService).search(PageRequest.of(0,5),"Test", "ua");
        assertEquals(response, searchEventDtoPageableDto);
    }

    @Test
    void searchAllEventsTest () {
        List<SearchEventDto> eventList = new ArrayList<>();
        eventList.add(SearchEventDto.builder()
            .id(1L)
            .title("title1")
            .organizer(new EventAuthorDto(1L, "Test1", 1D))
            .creationDate(LocalDate.now())
            .tags(ModelUtils.getTags().stream().map(Tag::toString).collect(Collectors.toList()))
            .build());
        eventList.add(SearchEventDto.builder()
            .id(2L)
            .title("title2")
            .organizer(new EventAuthorDto(2L, "Test2", 2D))
            .creationDate(LocalDate.now())
            .tags(ModelUtils.getTags().stream().map(Tag::toString).collect(Collectors.toList()))
            .build());
        PageableDto<SearchEventDto> searchEventDtoPageableDto = new PageableDto<>(eventList,eventList.size(),0,1);

        when(eventService.search(any(Pageable.class), anyString(), anyString())).thenReturn(searchEventDtoPageableDto);

        var response = searchService.searchAllEvents(PageRequest.of(0,5),"Test", "ua");

        verify(eventService).search(PageRequest.of(0,5),"Test", "ua");
        assertEquals(response, searchEventDtoPageableDto);
    }
}