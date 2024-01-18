package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationSourceType;
import greencity.service.NotificationService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
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
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void getTheLatestThreeNotifications() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(link + "/latest")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(notificationService).getTheLatestThreeNotifications(userVO.getId());
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

    @Test
    void getLikesForCurrentUser() throws Exception {
        List<NotificationsDto> notifications = List.of(NotificationsDto.builder()
                .userName("TEST")
                .objectTitle("TEST")
                .notificationTime(ZonedDateTime.now())
                .build());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(notificationService.getNotificationsForCurrentUser(userVO.getId(), NotificationSourceType.NEWS_LIKED)).thenReturn(notifications);

        mockMvc.perform(get(link + "/likes/eco-news")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(notificationService).getNotificationsForCurrentUser(userVO.getId(), NotificationSourceType.NEWS_LIKED);
    }

    @Test
    void getCommentsForCurrentUser() throws Exception {
        List<NotificationsDto> notifications = List.of(NotificationsDto.builder()
                .userName("TEST")
                .objectTitle("TEST")
                .notificationTime(ZonedDateTime.now())
                .build());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(notificationService.getNotificationsForCurrentUser(userVO.getId(), NotificationSourceType.NEWS_COMMENTED)).thenReturn(notifications);

        mockMvc.perform(get(link + "/comments")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(notificationService).getNotificationsForCurrentUser(userVO.getId(), NotificationSourceType.NEWS_COMMENTED);
    }

    @Test
    public void testGetLastLikesForEcoNewsComments() throws Exception {
        List<NotificationsDto> mockNotificationList = new ArrayList<>();

        when(notificationService.getNotificationsForCurrentUser(anyLong(), eq(NotificationSourceType.COMMENT_LIKED)))
                .thenReturn(mockNotificationList);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(link + "/likes/eco-news-comments")
                        .principal(userVO::getEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).getNotificationsForCurrentUser(anyLong(), eq(NotificationSourceType.COMMENT_LIKED));
    }
}