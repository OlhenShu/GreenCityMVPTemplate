package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationSourceType;
import greencity.service.NotificationService;
import greencity.service.UserService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {
    private static final String link = "/notifications";
    @InjectMocks
    private NotificationController notificationController;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    private MockMvc mockMvc;
    private final UserVO userVO = ModelUtils.getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void deleteTest () throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(link + "/{notificationId}",2L)
                .principal(userVO::getEmail))
            .andExpect(status().isOk());
        verify(notificationService).delete(2L, userVO);

    }
}
