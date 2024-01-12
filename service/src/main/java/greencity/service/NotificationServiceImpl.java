package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static greencity.enums.NotificationSourceType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserService userService;
    private final NotificationDtoResponseMapper mapper;
    private final NotifiedUserRepo notifiedUserRepo;
    private final ObjectMapper objectMapper;


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
    public PageableDto<NotificationDtoResponse> findAllByUser(Long userId, Pageable page) {
        Page<NotificationDtoResponse> notificationDtoPage = notificationRepo
                .findAllReceivedNotificationDtoByUserId(userId, page);

        List<NotificationDtoResponse> content = notificationDtoPage.getContent();

        List<Long> unreadNotificationsIds = content.stream()
                .filter(v -> v.getIsRead().equals(false))
                .map(NotificationDtoResponse::getId)
                .collect(Collectors.toList());

        List<NotifiedUser> notifiedUsers = notifiedUserRepo.findByUserIdAndNotificationIdIn(userId, unreadNotificationsIds);

        notifiedUsers.forEach(notifiedUser -> notifiedUser.setIsRead(true));

        notifiedUserRepo.saveAll(notifiedUsers);
        log.info("Update statuses for {} notifications", notifiedUsers.size());

        return new PageableDto<>(
                content,
                notificationDtoPage.getTotalElements(),
                notificationDtoPage.getPageable().getPageNumber(),
                notificationDtoPage.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void markAsReadNotification(Long userId, Long notificationId) {
        NotifiedUser notifiedUser = notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new NotFoundException("Notified user or notification not found"));
        if (notifiedUser.getIsRead()) {
            throw new BadRequestException(ErrorMessage.NOTIFICATION_ALREADY_READ);
        }
        notifiedUser.setIsRead(true);
        log.info("Set flag isRead: {}", notifiedUser.getIsRead());
        notifiedUserRepo.save(notifiedUser);
        log.info("Successfully update status");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void readLatestNotification(Long userId) {
        List<Notification> unreadNotificationsForUser = notifiedUserRepo.findTop3UnreadNotificationsForUser(userId);
        if (unreadNotificationsForUser.isEmpty()) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND_UNREAD_NOTIFICATION);
        }

        List<Long> notificationsIds = unreadNotificationsForUser.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());

        List<NotifiedUser> userNotifications = notifiedUserRepo.findByUserIdAndNotificationIdIn(userId, notificationsIds);

        userNotifications.forEach(notifiedUser -> notifiedUser.setIsRead(true));

        notifiedUserRepo.saveAll(userNotifications);
        log.info("Updated statuses for latest 3 unread notifications");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public NotificationDtoResponse createNewNotification(Long authorId, NewNotificationDtoRequest request) {
        User author = objectMapper.convertValue(userService.findById(authorId), User.class);
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
        User author = objectMapper.convertValue(userService.findById(authorId), User.class);
        notificationRepo.save(createFriendNotification(author));

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

    private Notification createFriendNotification(User author) {
        return Notification.builder()
                .creationDate(ZonedDateTime.now())
                .title(FRIEND_REQUEST.name())
                .author(author)
                .sourceType(FRIEND_REQUEST)
                .sourceId(3L)
                .build();
    }
}