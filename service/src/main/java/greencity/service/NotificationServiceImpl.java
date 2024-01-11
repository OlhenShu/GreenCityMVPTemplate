package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationSourceType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotifiedUserServiceImpl notifiedUserService;
    private final UserRepo userRepo;
    private final NotificationDtoResponseMapper mapper;


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId) {
        return notificationRepo.findTop3ByReceiversIdOrderByCreationDate(receiverId, PageRequest.of(0, 3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public NotificationDtoResponse createNewNotification(Long authorId, NewNotificationDtoRequest request) {
        User author = validateUserExist(authorId);
        Notification newNotification = Notification.builder()
                .creationDate(ZonedDateTime.now())
                .title(request.getTitle())
                .author(author)
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .build();
        Notification savedNotification = notificationRepo.save(newNotification);
        log.info("Notification with id {} saved", savedNotification.getId());
        return mapper.convert(savedNotification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void friendRequestNotification(Long authorId, Long friendId) {
        User author = validateUserExist(authorId);
        Notification savedNotification = notificationRepo.save(createFriendNotification(author));
        notifiedUserService.notifyUser(friendId, mapper.convert(savedNotification));

    }

    @Override
    public NotificationDtoResponse findById(Long notificationId) {
        Notification notification =
                notificationRepo.findById(notificationId)
                        .orElseThrow(
                                () -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId)
                        );
        return mapper.convert(notification);
    }

    private User validateUserExist(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId)
                );
    }

    private Notification createFriendNotification(User author) {
        return Notification.builder()
                .creationDate(ZonedDateTime.now())
                .title(NotificationSourceType.FRIEND_REQUEST.name())
                .author(author)
                .sourceType(NotificationSourceType.FRIEND_REQUEST)
                .sourceId(3L)
                .build();
    }
}