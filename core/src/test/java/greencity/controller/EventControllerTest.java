package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventsController eventsController;
    @Mock
    UserService userService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final UserVO userVO = ModelUtils.getUserVO();
    private final String eventLink = "/events";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(eventsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }
    @Test
    void save() throws Exception {
//        Principal principal = Mockito.mock(Principal.class);
//        when(principal.getName()).thenReturn(userVO.getEmail());
//        when(userService.findByEmail(userVO.getEmail())).thenReturn(userVO);
//        String json = "{" +
//                "\"title\": \"How to Promote Eco-Friendly Events on Social Media\"," +
//                "\"description\": \"How to Promote Eco-Friendly Events on Social Media\"," +
//                "\"open\": true," +
//                "\"datesLocations\": [" +
//                "{" +
//                "\"id\": 1," +
//                "\"startDate\": \"2024-01-17T06:00Z[UTC]\"," +
//                "\"finishDate\": \"2024-01-17T06:00Z[UTC]\"," +
//                "\"onlineLink\": \"http://localhost:8080/swagger-ui.html#/\"" +
//                "}" +
//                "]" +
//                "}";
//        MockMultipartFile jsonFile =
//                new MockMultipartFile("requestAddEventDto", "", "application/json", json.getBytes());
//        this.mockMvc.perform(multipart(eventLink + "/create")
//                        .file(jsonFile)
//                        .principal(principal)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//        objectMapper.readValue(json, AddEcoNewsDtoRequest.class);
//        verify(eventService).save(eq(ModelUtils.getRequestAddEventDto()), eq(userVO), eq(new ArrayList<>()));
    }
}
