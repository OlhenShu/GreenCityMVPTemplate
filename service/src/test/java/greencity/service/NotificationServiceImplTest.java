package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.enums.NotificationSourceType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotificationRepo;
import greencity.repository.NotifiedUserRepo;
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
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private final UserVO userVO = ModelUtils.getUserVO();

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

        PageableDto<NotificationDtoResponse> pageableDto = notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10));
        PageableDto<NotificationDtoResponse> expectedPageableDto = new PageableDto<>(notificationDtoList, 2L, 0, 1);

        verify(notificationRepo).findAllReceivedNotificationDtoByUserId(userVO.getId(), PageRequest.of(0, 10));
        assertEquals(expectedPageableDto, pageableDto);
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
        Page<NotificationDtoResponse> pagedNotifications = new PageImpl<>(notifications, PageRequest.of(0, 10), 2L);
        when(notificationRepo.findAllFriendRequestsByUserId(userId, page)).thenReturn(pagedNotifications);

        PageableDto<NotificationDtoResponse> result = notificationService.findAllFriendRequestsByUserId(userId, page);

        verify(notificationRepo, times(1)).findAllFriendRequestsByUserId(userId, page);
        verifyNoMoreInteractions(notificationRepo);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void testFriendRequestNotification() {
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
        Notification notification = new Notification(2L, user, title, ZonedDateTime.now(),
                NotificationSourceType.FRIEND_REQUEST, 1L, List.of(new NotifiedUser())); // You need to create an instance of Notification with some data
        var notificationDtoResponse = new NotificationDtoResponse(2L, 2L, "name1", title,
                NotificationSourceType.FRIEND_REQUEST, 1L, false, ZonedDateTime.now());

        Mockito.when(notificationRepo.findById(notificationId)).thenReturn(of(notification));
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
}
