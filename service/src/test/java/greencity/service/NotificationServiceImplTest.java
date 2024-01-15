package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.enums.NotificationSourceType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import greencity.repository.NotifiedUserRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private NotifiedUserRepo notifiedUserRepo;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private final NotificationDtoResponseMapper mapper = new NotificationDtoResponseMapper();
    private final UserVO userVO = ModelUtils.getUserVO();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notificationService = new NotificationServiceImpl(notificationRepo, userRepo, mapper, notifiedUserRepo);
    }

    @Test
    public void getTheLatestThreeNotifications() {
        var expected = List.of(
                new ShortNotificationDtoResponse(1L, "title", true),
                new ShortNotificationDtoResponse(2L, "title", true)
        );

        when(notificationRepo.findTop3ByReceiversIdOrderByCreationDate(anyLong(), any(Pageable.class)))
                .thenReturn(expected);

        var actual = notificationService.getTheLatestThreeNotifications(userVO.getId());

        verify(notificationRepo).findTop3ByReceiversIdOrderByCreationDate(userVO.getId(), PageRequest.of(0, 3));
        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserTest() {
        List<NotificationDtoResponse> notificationDtoList = new ArrayList<>();
        notificationDtoList.add(
                new NotificationDtoResponse(2L, 2L, "name1", "title",
                        NotificationSourceType.NEWS_LIKED, 1L, false, ZonedDateTime.now()));
        notificationDtoList.add(
                new NotificationDtoResponse(3L, 1L, "name2", "title",
                        NotificationSourceType.NEWS_COMMENTED, 23L, true, ZonedDateTime.now()));

        Page<NotificationDtoResponse> notificationDtoPage = new PageImpl<>(notificationDtoList, PageRequest.of(0, 10), 2L);

        when(notificationRepo.findAllReceivedNotificationDtoByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(notificationDtoPage);
        when(notifiedUserRepo.findByUserIdAndNotificationIdIn(anyLong(), anyList())).thenReturn(List.of());

        PageableDto<NotificationDtoResponse> pageableDto = notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10));
        PageableDto<NotificationDtoResponse> expectedPageableDto = new PageableDto<>(notificationDtoList, 2L, 0, 1);

        verify(notificationRepo).findAllReceivedNotificationDtoByUserId(userVO.getId(), PageRequest.of(0, 10));
        assertEquals(expectedPageableDto, pageableDto);
    }

    @Test
    void markAsReadNotificationByUserIdAndNotificationId() {
        Long userId = 10L;
        Long notificationId = 2L;

        NotifiedUser notifiedUser = NotifiedUser.builder()
                .isRead(false)
                .notification(Notification.builder()
                        .notifiedUsers(List.of())
                        .sourceType(NotificationSourceType.COMMENT_REPLY)
                        .id(notificationId)
                        .title("TEST")
                        .author(ModelUtils.getUser())
                        .creationDate(ZonedDateTime.now())
                        .sourceId(1L)
                        .build())
                .id(userId)
                .user(ModelUtils.getUser().setId(2L))
                .build();

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId)).thenReturn(java.util.Optional.of(notifiedUser));

        notificationService.markAsReadNotification(userId, notificationId);

        verify(notifiedUserRepo, times(1)).save(notifiedUser);
        assertTrue(notifiedUser.getIsRead());
    }

    @Test
    void markAsReadNotificationWhenNotificationAlreadyReadShouldThrowException() {
        Long userId = 1L;
        Long notificationId = 2L;

        NotifiedUser notifiedUser = new NotifiedUser();
        notifiedUser.setUser(ModelUtils.getUser());
        notifiedUser.setId(notificationId);
        notifiedUser.setIsRead(true);

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId)).thenReturn(java.util.Optional.of(notifiedUser));

        assertThrows(BadRequestException.class, () -> notificationService.markAsReadNotification(userId, notificationId));

        verify(notifiedUserRepo, never()).save(any());
    }

    @Test
    void markAsReadNotificationWhenNotificationNotFoundShouldThrowException() {
        Long userId = 1L;
        Long notificationId = 2L;

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.markAsReadNotification(userId, notificationId));

        verify(notifiedUserRepo, never()).save(any());
    }

    @Test
    void readLatestNotification() {
        Long userId = 1L;
        Notification notification1 = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .build();
        Notification notification2 = Notification.builder()
                .id(2L)
                .title("Notification 2")
                .build();
        Notification notification3 = Notification.builder()
                .id(3L)
                .title("Notification 3")
                .build();
        List<Notification> unreadNotificationsForUser = Arrays.asList(notification1, notification2, notification3);

        when(notifiedUserRepo.findTop3UnreadNotificationsForUser(userId)).thenReturn(unreadNotificationsForUser);

        List<Long> notificationIds = Arrays.asList(1L, 2L, 3L);
        List<NotifiedUser> userNotifications = Arrays.asList(
                NotifiedUser.builder()
                        .id(userId)
                        .notification(notification1)
                        .isRead(false)
                        .build(),
                NotifiedUser.builder()
                        .id(userId)
                        .notification(notification2)
                        .isRead(false)
                        .build(),
                NotifiedUser.builder()
                        .id(userId)
                        .notification(notification3)
                        .isRead(false)
                        .build()
        );

        when(notifiedUserRepo.findByUserIdAndNotificationIdIn(userId, notificationIds)).thenReturn(userNotifications);

        notificationService.readLatestNotification(userId);

        verify(notifiedUserRepo, times(1)).saveAll(userNotifications);
        assertTrue(userNotifications.stream().allMatch(NotifiedUser::getIsRead));
    }

    @Test
    void readLatestNotificationWhenNoUnreadNotificationsShouldThrowException() {
        Long userId = 1L;

        when(notifiedUserRepo.findTop3UnreadNotificationsForUser(userId)).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> notificationService.readLatestNotification(userId));

        verify(notifiedUserRepo, never()).saveAll(anyList());
    }

    @Test
    void createNewNotification() {
        Long authorId = userVO.getId();

        NewNotificationDtoRequest request = NewNotificationDtoRequest.builder()
                .title("Test Notification")
                .sourceType(NotificationSourceType.NEWS_COMMENTED)
                .sourceId(123L)
                .creationDate(ZonedDateTime.now())
                .build();

        User author = ModelUtils.getUser();

        Notification savedNotification = Notification.builder()
                .id(1L)
                .author(author)
                .creationDate(request.getCreationDate())
                .title(request.getTitle())
                .sourceId(request.getSourceId())
                .sourceType(request.getSourceType())
                .notifiedUsers(List.of())
                .build();

        when(userRepo.findById(authorId)).thenReturn(Optional.of(author));
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);

        NotificationDtoResponse result = notificationService.createNewNotification(authorId, request);

        assertNotNull(result);
        verify(userRepo, times(1)).findById(authorId);
        verify(notificationRepo, times(1)).save(any(Notification.class));

        assertEquals(request.getTitle(), savedNotification.getTitle());
        assertEquals(request.getSourceType(), savedNotification.getSourceType());
        assertEquals(request.getSourceId(), savedNotification.getSourceId());
    }

    @Test
    void createNewNotificationWhenUserNotFoundShouldThrowNotFoundException() {
        Long authorId = 1L;
        NewNotificationDtoRequest request = NewNotificationDtoRequest.builder()
                .creationDate(ZonedDateTime.now())
                .sourceId(1L)
                .title("Test")
                .sourceType(NotificationSourceType.NEWS_COMMENTED)
                .build();

        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.createNewNotification(authorId, request));

        verify(notificationRepo, never()).save(any(Notification.class));
    }
}