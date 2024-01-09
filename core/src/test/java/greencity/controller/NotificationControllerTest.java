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
    void findAllByUserTest() throws Exception {
        List<NotificationDtoResponse> notificationDtoList = new ArrayList<>();
        notificationDtoList.add(
            new NotificationDtoResponse(2L, 2L, "name1", "title",
                NotificationSourceType.NEWS_LIKED, 1L, false, ZonedDateTime.now()));
        notificationDtoList.add(
            new NotificationDtoResponse(3L, 1L, "name2", "title",
                NotificationSourceType.NEWS_COMMENTED, 23L, true, ZonedDateTime.now()));

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10)))
            .thenReturn(new PageableDto<>(notificationDtoList, 2L, 0, 1));

        mockMvc.perform(get(link)
            .principal(userVO::getEmail)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk());
        verify(notificationService).findAllByUser(userVO.getId(), PageRequest.of(0, 10));
    }
}
