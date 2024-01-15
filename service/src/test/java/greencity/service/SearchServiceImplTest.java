package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.Tag;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private EventService eventService;
    @InjectMocks
    private SearchServiceImpl searchService;

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
