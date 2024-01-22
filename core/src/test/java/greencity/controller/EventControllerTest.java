package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.*;
import java.security.Principal;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventControllerTest {
    private static final String eventsLink = "/events";
    private MockMvc mockMvc;
    @InjectMocks
    private EventsController eventsController;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    private Principal principal = getPrincipal();
    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final UserVO userVO = ModelUtils.getUserVO();
    private static final String eventLink = "/events";

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(eventsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    void findAmountOfEventsTest() throws Exception {
        UserVO userVO = getUserVO();
        mockMvc.perform(get(eventsLink + "/count")
                .param("userId",String.valueOf(userVO.getId())))
            .andExpect(status().isOk());

        verify(eventService).getAmountOfEvents(userVO.getId());
        }

    @Test
    void save() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn(userVO.getEmail());
        when(userService.findByEmail(userVO.getEmail())).thenReturn(userVO);
        String json = "{" +
                "\"title\": \"Eco-Friendly Events Social Media\"," +
                "\"description\": \"How to Promote Eco-Friendly Events on Social Media\"," +
                "\"open\": true," +
                "\"datesLocations\": [" +
                "{" +
                "\"id\": 1," +
                "\"startDate\": \"2024-01-17T06:00Z[UTC]\"," +
                "\"finishDate\": \"2024-01-17T06:00Z[UTC]\"," +
                "\"onlineLink\": \"http://localhost:8080/swagger-ui.html#/\"" +
                "}" +
                "]" +
                "}";
        MockMultipartFile jsonFile =
                new MockMultipartFile("addEventDtoRequest", "", "application/json", json.getBytes());
        this.mockMvc.perform(multipart(eventLink + "/create")
                        .file(jsonFile)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        objectMapper.readValue(json, AddEcoNewsDtoRequest.class);
        verify(eventService).save(ModelUtils.getRequestAddEventDto(), userVO, new MultipartFile[0]);
    }
    @Test
    void testCreateEvent() {
        AddEventDtoRequest addEventDtoRequest = new AddEventDtoRequest();
        MultipartFile[] images = new MultipartFile[0];

        UserVO mockUserVO = new UserVO();

        EventDto mockEventDto = new EventDto();
        when(eventService.save(addEventDtoRequest, mockUserVO, images)).thenReturn(mockEventDto);

        ResponseEntity<EventDto> response = eventsController.save(addEventDtoRequest, mockUserVO, images);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockEventDto, response.getBody());

        verify(eventService, times(1)).save(addEventDtoRequest, mockUserVO, images);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGetEvent() {
        Long eventId = 1L;
        EventDto mockEventDto = new EventDto();

        when(eventService.getById(eventId)).thenReturn(mockEventDto);

        ResponseEntity<EventDto> response = eventsController.getEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockEventDto, response.getBody());

        verify(eventService, times(1)).getById(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testUpdate() {
        UpdateEventDto updateEventDto = new UpdateEventDto();

        Principal principal = mock(Principal.class);

        MultipartFile[] images = new MultipartFile[0];

        when(principal.getName()).thenReturn(userVO.getEmail());
        when(eventService.update(updateEventDto, userVO.getEmail(), images)).thenReturn(new EventDto());

        ResponseEntity<EventDto> response = eventsController.update(updateEventDto, principal, images);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(eventService, times(1)).update(updateEventDto, principal.getName(), images);
        verifyNoMoreInteractions(eventService);
    }
}
