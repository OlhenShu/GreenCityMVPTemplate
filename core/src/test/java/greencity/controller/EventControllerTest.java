package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.*;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
}
