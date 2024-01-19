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
import greencity.repository.EventRepo;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.TEST_USER_VO;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getEventTagVO());
    List<Tag> tags = Collections.singletonList(ModelUtils.getEventTag());
    MultipartFile file = ModelUtils.getFile();
    MultipartFile[] multipartFiles = new MultipartFile[] {file};

    @Test
    void saveTest() throws Exception {
        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        EventDateLocationDto eventDateLocationDto =  addEventDtoRequest.getDatesLocations().get(0);
        AddressDto addressDto = eventDateLocationDto.getCoordinates();
        AddressLatLngResponse response = AddressLatLngResponse.builder()
                .latitude(addressDto.getLatitude())
                .longitude(addressDto.getLongitude())
                .build();
        LatLng latLng = new LatLng(addressDto.getLatitude(),addressDto.getLongitude());
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
        User user = ModelUtils.getUser();
        user.setRole(Role.ROLE_ADMIN);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);

        when(modelMapper.map(expectedEvent, EventDto.class)).thenReturn(eventDto);
        when(eventRepo.save(expectedEvent)).thenReturn(expectedEvent);

        EventDto actualEvent = eventService.update(eventToUpdateDto, user.getEmail(), null);

        assertEquals(eventDto, actualEvent);
    }
}
