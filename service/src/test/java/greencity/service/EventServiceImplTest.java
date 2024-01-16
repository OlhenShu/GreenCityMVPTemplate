package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import greencity.entity.event.Event;
import greencity.repository.EventSearchRepo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EventSearchRepo eventSearchRepo;
    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void searchTest () {
        List<Event> eventList = new ArrayList<>();
        eventList.add(Event.builder()
            .id(1L)
            .title("title1")
            .description("description1")
            .titleImage("img")
            .open(false)
            .organizer(ModelUtils.getUser())
            .creationDate(LocalDate.now())
            .tags(ModelUtils.getTags())
            .build());
        eventList.add(Event.builder()
            .id(2L)
            .title("title2")
            .description("description2")
            .titleImage("img")
            .open(false)
            .organizer(ModelUtils.getUser())
            .creationDate(LocalDate.now())
            .tags(ModelUtils.getTags())
            .build());
        Page<Event> eventPage = new PageImpl<>(eventList, PageRequest.of(0,5), eventList.size());

        when(eventSearchRepo.find(any(Pageable.class), anyString(), anyString())).thenReturn(eventPage);
        ModelMapper modelMapper1 = new ModelMapper();
        when(modelMapper.map(eventList.get(0), SearchEventDto.class))
            .thenReturn(modelMapper1.map(eventList.get(0), SearchEventDto.class));
        when(modelMapper.map(eventList.get(1), SearchEventDto.class))
            .thenReturn(modelMapper1.map(eventList.get(1), SearchEventDto.class));

        var eventDtoPage = eventService.search(PageRequest.of(0,10), "Test", "ua");

        var expectedEventDtoPage = eventPage.stream().map(event -> modelMapper.map(event, SearchEventDto.class)).collect(
            Collectors.toList());

        PageableDto<SearchEventDto> searchEventDtoPageableDto =
            new PageableDto<>(expectedEventDtoPage, expectedEventDtoPage.size(), 0, 1);

        verify(eventSearchRepo).find(PageRequest.of(0,10), "Test", "ua");
        assertEquals(searchEventDtoPageableDto, eventDtoPage);
    }
}
