package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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
    void createNewNotification() throws Exception {
        NewNotificationDtoRequest request = NewNotificationDtoRequest.builder()
                .title("TEST")
                .sourceType(NotificationSourceType.NEWS_COMMENTED)
                .sourceId(1L)
                .build();
        NotificationDtoResponse response = NotificationDtoResponse.builder()
                .id(1L)
                .author(AuthorDto.builder()
                        .id(1L)
                        .name("Author")
                        .build())
                .title(request.getTitle())
                .creationDate(ZonedDateTime.now())
                .sourceType(request.getSourceType())
                .isRead(false)
                .sourceId(request.getSourceId())
                .build();

        when(notificationService.createNewNotification(eq(1L), any(NewNotificationDtoRequest.class))).thenReturn(response);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(link + "/create")
                        .principal(userVO::getEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(notificationService).createNewNotification(userVO.getId(), request);
    }

    @Test
    void marsAsReadNotificationById() throws Exception {
        Notification notification = Notification.builder()
                .id(1L)
                .creationDate(ZonedDateTime.now())
                .title("TEST")
                .sourceType(NotificationSourceType.NEWS_COMMENTED)
                .sourceId(1L)
                .author(ModelUtils.getUser())
                .build();

        doNothing().when(notificationService).markAsReadNotification(anyLong(), anyLong());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(link + "/mark-as-read/{id}", notification.getId())
                        .principal(userVO::getEmail)
                        .param("id", notification.getId().toString()))
                .andExpect(status().isOk());

        verify(notificationService).markAsReadNotification(userVO.getId(), notification.getId());
    }

    @Test
    void markAsReadLast3UnreadNotification() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doNothing().when(notificationService).readLatestNotification(eq(userVO.getId()));

        mockMvc.perform(patch(link + "/mark-as-read/")
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());

        verify(notificationService).readLatestNotification(userVO.getId());
    }
}