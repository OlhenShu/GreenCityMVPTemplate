package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsDto;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.enums.NotificationSourceType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotificationRepo;
import greencity.repository.NotifiedUserRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private NotifiedUserRepo notifiedUserRepo;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private UserRepo userRepo;
    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    void getTheLatestThreeNotifications() {
        var expected = List.of(
                new ShortNotificationDtoResponse(1L, "title", true),
                new ShortNotificationDtoResponse(2L, "title", true)
        );

        when(notificationRepo.findTop3ByReceiversIdOrderByCreationDate(anyLong(), any(Pageable.class)))
                .thenReturn(expected);

        var actual = notificationService.getTheLatestThreeNotifications(userVO.getId());

        verify(notificationRepo)
                .findTop3ByReceiversIdOrderByCreationDate(userVO.getId(), PageRequest.of(0, 3));
        assertEquals(expected, actual);
    }

    @Test
    void findAllByUserTest() {
        List<NotificationDtoResponse> notificationDtoList = new ArrayList<>();
        notificationDtoList.add(
                new NotificationDtoResponse(2L, 2L, "name1", "title",
                        NotificationSourceType.NEWS_LIKED, 1L, false, ZonedDateTime.now()));
        notificationDtoList.add(
                new NotificationDtoResponse(3L, 1L, "name2", "title",
                        NotificationSourceType.NEWS_COMMENTED, 23L, true, ZonedDateTime.now()));
        Page<NotificationDtoResponse> notificationDtoPage =
                new PageImpl<>(notificationDtoList, PageRequest.of(0, 10), 2L);

        when(notificationRepo.findAllReceivedNotificationDtoByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(notificationDtoPage);

        PageableDto<NotificationDtoResponse> pageableDto =
                notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10));
        PageableDto<NotificationDtoResponse> expectedPageableDto =
                new PageableDto<>(notificationDtoList, 2L, 0, 1);

        verify(notificationRepo).findAllReceivedNotificationDtoByUserId(userVO.getId(), PageRequest.of(0, 10));
        assertEquals(expectedPageableDto, pageableDto);
    }

    @Test
    void getNotificationsEcoNewsForCurrentUserReturnsNotificationsDtoList() {
        Long userId = 1L;
        NotifiedUser notifiedUser = buildNotifiedUser();
        List<NotifiedUser> allUnreadNotifications = Collections.singletonList(notifiedUser);

        when(notifiedUserRepo.findAllUnreadNotificationsByUserId(eq(userId), any())).thenReturn(allUnreadNotifications);

        List<NotificationsDto> result = notificationService.
                getNotificationsForCurrentUser(userId, NotificationSourceType.NEWS_LIKED);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        NotificationsDto notificationsForEcoNewsDto = result.get(0);
        assertEquals("AuthorName", notificationsForEcoNewsDto.getUserName());
        assertEquals("TestTitle", notificationsForEcoNewsDto.getObjectTitle());
        assertEquals(ZonedDateTime.parse("2022-01-01T10:15:30+01:00"),
                notificationsForEcoNewsDto.getNotificationTime());

        verify(notifiedUserRepo).findAllUnreadNotificationsByUserId(eq(userId), any());
    }

    @Test
    void findAllFriendRequestsByUserId() {
        Long userId = 1L;
        Pageable page = PageRequest.of(0, 10);
        List<NotificationDtoResponse> notifications = new ArrayList<>();
        notifications.add(
                new NotificationDtoResponse(2L, 2L, "name1", "title",
                        NotificationSourceType.FRIEND_REQUEST, 1L, false, ZonedDateTime.now()));
        notifications.add(
                new NotificationDtoResponse(3L, 1L, "name2", "title",
                        NotificationSourceType.FRIEND_REQUEST, 23L, true, ZonedDateTime.now()));
        Page<NotificationDtoResponse> pagedNotifications = new PageImpl<>(
                notifications, PageRequest.of(0, 10), 2L
        );
        when(notificationRepo.findAllFriendRequestsByUserId(userId, page)).thenReturn(pagedNotifications);

        PageableDto<NotificationDtoResponse> result = notificationService.findAllFriendRequestsByUserId(userId, page);

        verify(notificationRepo, times(1)).findAllFriendRequestsByUserId(userId, page);
        verifyNoMoreInteractions(notificationRepo);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testFriendRequestNotification() {
        Long authorId = 1L;
        Long friendId = 2L;

        when(userService.findById(authorId)).thenReturn(ModelUtils.getUserVO());
        when(userService.findById(friendId)).thenReturn(ModelUtils.getUserVO());

        when(modelMapper.map(any(), any())).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(any(), any())).thenReturn(ModelUtils.getUser());

        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        notificationService.friendRequestNotification(authorId, friendId);

        verify(userService, times(2)).findById(anyLong());
        verify(modelMapper, times(2)).map(any(), eq(User.class));
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(notifiedUserRepo, times(1)).save(any(NotifiedUser.class));
    }


    @Test
    void testFindById() {
        var user = ModelUtils.getUser();
        var title = "title";
        Long notificationId = 1L;

        User author = ModelUtils.getUser();

        Notification notification = new Notification(2L, user, title, ZonedDateTime.now(),
                NotificationSourceType.FRIEND_REQUEST, 1L, null);

        NotifiedUser savedUser = NotifiedUser.builder()
                .id(1L)
                .user(author)
                .notification(notification)
                .isRead(false)
                .build();
        notification.setNotifiedUsers(List.of(savedUser));

        var notificationDtoResponse = new NotificationDtoResponse(2L, 2L, "name1", title,
                NotificationSourceType.FRIEND_REQUEST, 1L, false, ZonedDateTime.now());

        Mockito.when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));
        Mockito.when(modelMapper.map(notification, NotificationDtoResponse.class)).thenReturn(notificationDtoResponse); // You need to create an instance of NotificationDtoResponse with some data


        NotificationDtoResponse result = notificationService.findById(notificationId);


        Mockito.verify(notificationRepo, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(modelMapper, Mockito.times(1)).map(notification, NotificationDtoResponse.class);
        assertEquals(notificationDtoResponse, result);
    }
    @Test
    void testFindByWrongId() {
        assertThrows(NotFoundException.class, () -> notificationService.findById(1L));
    }


    @Test
    void getNotificationsEcoNewsForCurrentUserThrowsNotFoundException() {
        Long userId = 1L;

        when(notifiedUserRepo.findAllUnreadNotificationsByUserId(eq(userId), any())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> notificationService.getNotificationsForCurrentUser(userId, NotificationSourceType.NEWS_LIKED));

        verify(notifiedUserRepo).findAllUnreadNotificationsByUserId(eq(userId), any());
    }

    @Test
    void createEcoNewsNotificationSuccessfullyCreatesNotification() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        NotificationSourceType sourceType = NotificationSourceType.NEWS_LIKED;

        User author = ModelUtils.getUser();

        Notification savedNotification = new Notification(2L, author, "title", ZonedDateTime.now(),
                NotificationSourceType.FRIEND_REQUEST, 1L, null);

        NotifiedUser savedUser = NotifiedUser.builder()
                .id(1L)
                .user(author)
                .notification(savedNotification)
                .isRead(false)
                .build();

        savedNotification.setNotifiedUsers(List.of(savedUser));

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(author));
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);
        when(userRepo.findById(ecoNewsVO.getAuthor().getId())).thenReturn(Optional.of(author));
        when(notifiedUserRepo.save(any(NotifiedUser.class))).thenReturn(savedUser);

        notificationService.createNotification(userVO, ecoNewsVO, sourceType);

        verify(userRepo, times(2)).findById(anyLong());
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(notifiedUserRepo, times(1)).save(any(NotifiedUser.class));
    }

    @Test
    void createEcoNewsNotificationThrowsNotFoundExceptionForUser() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        NotificationSourceType sourceType = NotificationSourceType.NEWS_LIKED;

        when(userRepo.findById(userVO.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.createNotification(userVO, ecoNewsVO, sourceType));

        verifyNoInteractions(notificationRepo, notifiedUserRepo);
    }

    @Test
    void createEcoNewsNotificationThrowsNotFoundExceptionForNewsAuthor() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        NotificationSourceType sourceType = NotificationSourceType.NEWS_LIKED;

        User author = ModelUtils.getUser().setId(1L);
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(author));

        when(notificationRepo.save(any(Notification.class))).thenThrow(new NotFoundException("User with id: 2 not found"));

        assertThrows(NotFoundException.class, () -> notificationService.createNotification(userVO, ecoNewsVO, sourceType));

        verify(userRepo).findById(userVO.getId());
        verify(notificationRepo).save(any(Notification.class));
        verifyNoInteractions(notifiedUserRepo);
    }

    private NotifiedUser buildNotifiedUser() {
        return NotifiedUser.builder()
                .isRead(false)
                .id(1L)
                .user(ModelUtils.getUser())
                .notification(Notification.builder()
                        .id(1L)
                        .creationDate(ZonedDateTime.parse("2022-01-01T10:15:30+01:00"))
                        .title("TestTitle")
                        .sourceType(NotificationSourceType.NEWS_LIKED)
                        .sourceId(1L)
                        .author(ModelUtils.getUser().setId(2L).setName("AuthorName"))
                        .notifiedUsers(Collections.emptyList())
                        .build())
                .build();
    }
}