package greencity.service;

import com.google.maps.model.LatLng;
import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.dto.PageableDto;
import greencity.dto.event.*;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.search.SearchEventDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.repository.EventSearchRepo;
import greencity.repository.UserRepo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.TypeUtils;
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
import org.springframework.web.multipart.MultipartFile;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EventSearchRepo eventSearchRepo;
    @InjectMocks
    private EventServiceImpl eventService;
    @Mock
    EventRepo eventRepo;
    @Mock
    TagsService tagService;
    @Mock
    FileService fileService;
    @Mock
    GoogleApiService googleApiService;
    @Mock
    private RestClient restClient;

    private final AddEventDtoRequest addEventDtoRequest = ModelUtils.getRequestAddEventDto();
    private final EventDto eventDto = ModelUtils.getEventDto();
    private final Event event = ModelUtils.getEvent();
    private final UserVO userVO = getUserVO();
    private final List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getEventTagVO());
    private final List<Tag> tags = Collections.singletonList(ModelUtils.getEventTag());
    private final MultipartFile file = ModelUtils.getFile();
    private final MultipartFile[] multipartFiles = new MultipartFile[] {file};
    private final EventDateLocationDto eventDateLocationDto = addEventDtoRequest.getDatesLocations().get(0);
    private final AddressDto addressDto = eventDateLocationDto.getCoordinates();
    private final AddressLatLngResponse response = AddressLatLngResponse.builder()
        .latitude(addressDto.getLatitude())
        .longitude(addressDto.getLongitude())
        .build();
    private final LatLng latLng = new LatLng(addressDto.getLatitude(), addressDto.getLongitude());

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


    @Test
    void getAmountOfEvents(){
        UserVO userVO = getUserVO();
        Long expected = 5L;

        when(userRepo.existsById(userVO.getId())).thenReturn(true);
        when(eventRepo.countByOrganizerId(userVO.getId())).thenReturn(expected);
        Long actual = eventService.getAmountOfEvents(userVO.getId());

        verify(userRepo).existsById(userVO.getId());
        verify(eventRepo).countByOrganizerId(userVO.getId());
        assertEquals(expected,actual);
    }

    @Test
    void getAmountOfEventsByNotExistingUserIdThrowsException(){
        UserVO userVO = getUserVO();
        when(userRepo.existsById(userVO.getId())).thenReturn(false);

        assertThrows(NotFoundException.class,
            () -> eventService.getAmountOfEvents(userVO.getId()));
        verify(userRepo).existsById(userVO.getId());
    }

    @Test
    void saveTest() throws Exception {
        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(googleApiService.getResultFromGeoCodeByCoordinates(latLng)).thenReturn(response);
        when(modelMapper.map(response, AddressDto.class)).thenReturn(addressDto);
        when(modelMapper.map(addEventDtoRequest.getDatesLocations().get(0), EventDateLocation.class)).thenReturn(event.getDates().get(0));
        when(modelMapper.map(getUserVO(), User.class)).thenReturn(ModelUtils.getUser());
        when(fileService.upload(file)).thenReturn(ModelUtils.getUrl().toString());
        when(eventRepo.save(event)).thenReturn(event);
        when(tagService.findAllTranslationsByNamesAndType(addEventDtoRequest.getTags(), TagType.EVENT)).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, TypeUtils.parameterize(List.class, Tag.class))).thenReturn(tags);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        EventDto actual = eventService.save(addEventDtoRequest, userVO, multipartFiles);

        assertEquals(eventDto, actual);
    }

    @Test
    void getByIdTest() {
        Long eventId = 1L;
        Event event = new Event();

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(new EventDto());

        EventDto result = eventService.getById(eventId);

        assertEquals(event.getId(), result.getId());
    }

    @Test
    void update() {
        EventDto eventDto = ModelUtils.getEventDto();
        Event expectedEvent = ModelUtils.getEvent();

        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        eventToUpdateDto.setDatesLocations(
                List.of(EventDateLocationDto.builder()
                        .id(1L)
                        .startDate(ZonedDateTime.parse("2026-01-17T06:00Z[UTC]"))
                        .finishDate(ZonedDateTime.parse("2026-01-19T06:00Z[UTC]"))
                        .onlineLink("http://localhost:8080/swagger-ui.html#/")
                        .coordinates(AddressDto.builder()
                                .latitude(45.466272)
                                .longitude(9.188604)
                                .build())
                        .build()));

        eventToUpdateDto.setTags(Collections.singletonList(getTagUaEnDto().getNameUa()));
        eventToUpdateDto.setTitle("TestTitle");
        eventToUpdateDto.setDescription("TestDescription");
        eventToUpdateDto.setOpen(true);

        User user = ModelUtils.getUser();
        user.setRole(Role.ROLE_ADMIN);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);

        when(modelMapper.map(expectedEvent, EventDto.class)).thenReturn(eventDto);
        when(eventRepo.save(expectedEvent)).thenReturn(expectedEvent);

        when(googleApiService.getResultFromGeoCodeByCoordinates(latLng)).thenReturn(response);
        when(modelMapper.map(response, AddressDto.class)).thenReturn(addressDto);
        when(modelMapper.map(eventToUpdateDto.getDatesLocations().get(0), EventDateLocation.class)).thenReturn(event.getDates().get(0));
        when(tagService.findAllTranslationsByNamesAndType(eventToUpdateDto.getTags(), TagType.EVENT)).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, TypeUtils.parameterize(List.class, Tag.class))).thenReturn(tags);

        EventDto actualEvent = eventService.update(eventToUpdateDto, user.getEmail(), new MultipartFile[0]);

        assertEquals(eventDto, actualEvent);
    }

    @Test
    void updateFinishedEvent() {
        Event finishedEventDto = ModelUtils.getEventWithFinishedDate();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        User user = ModelUtils.getUser();
        user.setRole(Role.ROLE_ADMIN);

        when(eventRepo.findById(any())).thenReturn(Optional.of(finishedEventDto));
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);

        assertThrows(BadRequestException.class,
                () -> {
                    eventService.update(eventToUpdateDto, user.getEmail(), null);
                });
    }

    @Test
    void updateThrowsUserHasNoPermissionToAccessException() {
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        eventToUpdateDto.setId(1L);

        UserVO userVO = new UserVO();
        userVO.setRole(Role.ROLE_USER);
        userVO.setEmail("test@gmail.com");
        userVO.setId(1L);

        User user = new User();
        user.setRole(Role.ROLE_USER);
        user.setEmail("test2@gmail.com");
        user.setId(2L);

        Event expectedEvent = ModelUtils.getEvent();
        expectedEvent.setId(1L);
        expectedEvent.setOrganizer(user);

        User user1 = new User();
        user1.setRole(Role.ROLE_USER);
        user1.setEmail("test@gmail.com");
        user1.setId(1L);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(modelMapper.map(userVO, User.class)).thenReturn(user1);
        when(restClient.findByEmail(anyString())).thenReturn(userVO);

        System.out.println("Expected Organizer Email: " + userVO.getEmail());

        assertThrows(UserHasNoPermissionToAccessException.class, () -> {
            eventService.update(eventToUpdateDto, userVO.getEmail(), null);
        });

        verify(eventRepo).findById(1L);
        verify(restClient).findByEmail("test@gmail.com");
        verify(modelMapper).map(userVO, User.class);
    }

    @Test
    void updatedEventWithNewData() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = EventServiceImpl.class.getDeclaredMethod("updatedEventWithNewData", Event.class,
                UpdateEventDto.class, MultipartFile[].class);
        method.setAccessible(true);
        Event event = ModelUtils.getEvent();
        Event expectedEvent = ModelUtils.getExpectedEvent();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(event.getTitleImage(), expectedEvent.getTitleImage());

        eventToUpdateDto.setTitleImage("New img");
        eventToUpdateDto.setAdditionalImages(List.of("New additional image"));
        expectedEvent.setTitleImage("New img");
        expectedEvent.setAdditionalImages(List.of(EventImages.builder().link("New additional image").build()));

        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
                event.getAdditionalImages().get(0).getLink());
        assertEquals(event.getTitleImage(), expectedEvent.getTitleImage());

        eventToUpdateDto.setImagesToDelete(List.of("New additional image"));
        doNothing().when(fileService).delete(any());

        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
                event.getAdditionalImages().get(0).getLink());

        eventToUpdateDto.setAdditionalImages(null);
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertNull(event.getAdditionalImages());

        eventToUpdateDto.setTitleImage(null);
        expectedEvent.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
    }
}
