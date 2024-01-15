package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationSourceType;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.NotificationRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private final UserVO userVO = ModelUtils.getUserVO();

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
}

