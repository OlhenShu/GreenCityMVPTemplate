package greencity.service;

import com.google.maps.model.LatLng;
import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.event.*;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.TEST_USER_VO;
import static greencity.ModelUtils.getTagUaEnDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    EventRepo eventRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    TagsService tagService;
    @Mock
    FileService fileService;
    @InjectMocks
    private EventServiceImpl eventService;
    @Mock
    GoogleApiService googleApiService;
    @Mock
    private RestClient restClient;

    private final AddEventDtoRequest addEventDtoRequest = ModelUtils.getRequestAddEventDto();
    private final EventDto eventDto = ModelUtils.getEventDto();
    private final Event event = ModelUtils.getEvent();
    private final UserVO userVO = ModelUtils.getUserVO();
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
    private final LatLng latLng = new LatLng(addressDto.getLatitude(),addressDto.getLongitude());

    @Test
    void saveTest() throws Exception {
        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(googleApiService.getResultFromGeoCodeByCoordinates(latLng)).thenReturn(response);
        when(modelMapper.map(response, AddressDto.class)).thenReturn(addressDto);
        when(modelMapper.map(addEventDtoRequest.getDatesLocations().get(0), EventDateLocation.class)).thenReturn(event.getDates().get(0));
        when(modelMapper.map(ModelUtils.getUserVO(), User.class)).thenReturn(ModelUtils.getUser());
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
                () -> eventService.update(eventToUpdateDto, user.getEmail(), null));
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

        assertThrows(UserHasNoPermissionToAccessException.class,
                () -> eventService.update(eventToUpdateDto, userVO.getEmail(), null));

        verify(eventRepo).findById(1L);
        verify(restClient).findByEmail("test@gmail.com");
        verify(modelMapper).map(userVO, User.class);
    }
}
