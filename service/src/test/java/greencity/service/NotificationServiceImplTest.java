package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsDto;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
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
import java.util.*;
import java.util.stream.Collectors;

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
                        NotificationSourceType.NEWS_COMMENTED, 0L, true, ZonedDateTime.now()));
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
    void getNotificationsEcoNewsForCurrentUserReturnsNotificationsDtoList() {
        NotificationSourceType sourceType = NotificationSourceType.NEWS_LIKED;

        NotifiedUser notifiedUser = buildNotifiedUser();


        when(notifiedUserRepo.findAllUnreadNotificationsByUserId(notifiedUser.getId(), sourceType))
                .thenReturn(Collections.singletonList(notifiedUser));

        List<NotificationsDto> result = notificationService.getNotificationsForCurrentUser(notifiedUser.getId(), sourceType);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(notifiedUserRepo, times(1)).findAllUnreadNotificationsByUserId(notifiedUser.getId(), sourceType);
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
                NotificationSourceType.FRIEND_REQUEST, 1L, NotificationSource.FRIENDS_REQUEST, null);

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
                NotificationSourceType.FRIEND_REQUEST, 1L, NotificationSource.FRIENDS_REQUEST, null);

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
                        .author(ModelUtils.getUser())
                        .notificationSource(NotificationSource.NEWS)
                        .notifiedUsers(Collections.emptyList())
                        .build())
                .build();
    }

    @Test
    void deleteTest () {
        Notification notification = Notification.builder()
            .id(1L)
            .author(ModelUtils.getUser())
            .build();

        when(notificationRepo.findById(anyLong())).thenReturn(Optional.of(notification));

        notificationService.delete(1L, userVO);

        verify(notificationRepo).deleteById(1L);
    }

    @Test
    void deleteWhenNotificationIsNotFoundThrowsNotFoundExceptionTest () {
        assertThrows(NotFoundException.class, () -> notificationService.delete(1L, userVO));
    }

    @Test
    void deleteWhenDeletingForeignNotificationWithoutAnAdminRoleThrowsUserHasNoPermissionToAccessExceptionTest () {
        User user = ModelUtils.getUser();
        user.setId(2L);
        Notification notification = Notification.builder()
            .id(1L)
            .author(user)
            .build();

        when(notificationRepo.findById(anyLong())).thenReturn(Optional.of(notification));

        assertThrows(UserHasNoPermissionToAccessException.class, () -> notificationService.delete(1L, userVO));
    }

    @Test
    void deleteWhenDeletingForeignNotificationWithAnAdminRoleTest () {
        User user = ModelUtils.getUser();
        user.setId(2L);
        user.setRole(Role.ROLE_ADMIN);
        Notification notification = Notification.builder()
            .id(1L)
            .author(user)
            .build();

        when(notificationRepo.findById(anyLong())).thenReturn(Optional.of(notification));

        assertThrows(UserHasNoPermissionToAccessException.class, () -> notificationService.delete(1L, userVO));
    }

    @Test
    void testMarkAsReadNotification() {
        Long userId = 1L;
        Long notificationId = 1L;

        NotifiedUser notifiedUser = buildNotifiedUser();

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId))
                .thenReturn(Optional.of(notifiedUser));

        assertDoesNotThrow(() -> notificationService.markAsReadNotification(userId, notificationId));

        assertTrue(notifiedUser.getIsRead());

        verify(notifiedUserRepo, times(1)).findByUserIdAndNotificationId(userId, notificationId);
        verify(notifiedUserRepo, times(1)).save(notifiedUser);
    }

    @Test
    void testMarkAsReadNotification_NotFound() {
        Long userId = 1L;
        Long notificationId = 1L;

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> notificationService.markAsReadNotification(userId, notificationId));

        verify(notifiedUserRepo, times(1)).findByUserIdAndNotificationId(userId, notificationId);
        verifyNoMoreInteractions(notifiedUserRepo);
    }

    @Test
    void testMarkAsReadNotification_AlreadyRead() {
        Long userId = 1L;
        Long notificationId = 1L;

        NotifiedUser notifiedUser = new NotifiedUser();
        notifiedUser.setIsRead(true);

        when(notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId))
                .thenReturn(Optional.of(notifiedUser));

        assertThrows(BadRequestException.class,
                () -> notificationService.markAsReadNotification(userId, notificationId));

        verify(notifiedUserRepo, times(1)).findByUserIdAndNotificationId(userId, notificationId);
        verifyNoMoreInteractions(notifiedUserRepo);
    }

    @Test
    void testReadLatestNotification() {
        Long userId = 1L;
        User user = ModelUtils.getUser();
        NotifiedUser notifiedUser = buildNotifiedUser();


        List<Notification> unreadNotifications = new ArrayList<>();
        unreadNotifications.add(
                new Notification(1L, user, "name1", ZonedDateTime.now(),
                        NotificationSourceType.NEWS_LIKED, 1L,
                        NotificationSource.NEWS, List.of(notifiedUser)));
        unreadNotifications.add(
                new Notification(2L, user, "name2", ZonedDateTime.now(),
                        NotificationSourceType.NEWS_LIKED, 1L,
                        NotificationSource.NEWS, List.of(notifiedUser)));
        unreadNotifications.add(
                new Notification(3L, user, "name3", ZonedDateTime.now(),
                        NotificationSourceType.NEWS_LIKED, 1L,
                        NotificationSource.NEWS, List.of(notifiedUser)));

        when(notifiedUserRepo.findTop3UnreadNotificationsForUser(userId))
                .thenReturn(unreadNotifications);

        List<NotifiedUser> userNotifications = unreadNotifications.stream()
                .map(notification -> {
                    notifiedUser.setNotification(notification);
                    return notifiedUser;
                })
                .collect(Collectors.toList());

        when(notifiedUserRepo.findByUserIdAndNotificationIdIn(userId,
                List.of(1L, 2L, 3L)))
                .thenReturn(userNotifications);

        assertDoesNotThrow(() -> notificationService.readLatestNotification(userId));

        assertTrue(userNotifications.stream().allMatch(NotifiedUser::getIsRead));

        verify(notifiedUserRepo, times(1)).findTop3UnreadNotificationsForUser(userId);
        verify(notifiedUserRepo, times(1)).findByUserIdAndNotificationIdIn(userId,
                List.of(1L, 2L, 3L));
        verify(notifiedUserRepo, times(1)).saveAll(userNotifications);
    }

    @Test
    void testReadLatestNotification_NoUnreadNotifications() {
        Long userId = 1L;

        when(notifiedUserRepo.findTop3UnreadNotificationsForUser(userId))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> notificationService.readLatestNotification(userId));

        verify(notifiedUserRepo, times(1)).findTop3UnreadNotificationsForUser(userId);
        verifyNoMoreInteractions(notifiedUserRepo);
    }

    @Test
    void testCreateNotification_EcoNewsComment() {

        UserVO userVO = ModelUtils.getUserVO();

        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

        EcoNews ecoNews = ModelUtils.getEcoNews();
        ecoNewsComment.setEcoNews(ecoNews);

        NotifiedUser notifiedUser = buildNotifiedUser();

        Notification notification = new Notification(1L, ModelUtils.getUser(), "name1", ZonedDateTime.now(),
                NotificationSourceType.NEWS_LIKED, 1L,
                NotificationSource.NEWS, List.of(notifiedUser));

        when(userRepo.findById(userVO.getId()))
                .thenReturn(Optional.of(ModelUtils.getUser())); // mock user retrieval
        when(modelMapper.map(ecoNewsComment.getEcoNews().getAuthor(), UserVO.class))
                .thenReturn(userVO);
        when(notificationRepo.save(any())).thenReturn(notification);
        when(notifiedUserRepo.save(any(NotifiedUser.class))).thenReturn(notifiedUser);

        assertDoesNotThrow(() -> notificationService.createNotification(userVO, ecoNewsComment,
                NotificationSourceType.NEWS_COMMENTED));


        verify(userRepo, times(2)).findById(userVO.getId());
        verify(modelMapper, times(1)).map(ecoNewsComment.getEcoNews().getAuthor(), UserVO.class);
        verify(notificationRepo, times(1)).save(any(Notification.class));
        verify(userRepo, times(2)).findById(ModelUtils.getEcoNewsVO().getAuthor().getId());
        verify(modelMapper, times(1)).map(ModelUtils.getUser(), UserVO.class);
        verify(notifiedUserRepo, times(1)).save(any(NotifiedUser.class));


    }
}