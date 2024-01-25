package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.config.TelegramBotConfig;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.event.EventVO;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsDto;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNewsComment;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
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
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserService userService;
    private final NotificationDtoResponseMapper mapper;
    private final NotifiedUserRepo notifiedUserRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final TelegramBotConfig telegramBotConfig;
    private final EcoNewsRepo ecoNewsRepo;
    private final EventRepo eventRepo;

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

        List<NotifiedUser> notifiedUsers = notifiedUserRepo
                .findByUserIdAndNotificationIdIn(userId, unreadNotificationsIds);

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
    public PageableDto<NotificationDtoResponse> findAllFriendRequestsByUserId(Long userId, Pageable page) {
        Page<NotificationDtoResponse> allFriendRequestsByUserId =
                notificationRepo.findAllFriendRequestsByUserId(userId, page);

        List<NotificationDtoResponse> content = allFriendRequestsByUserId.getContent();

        return new PageableDto<>(
                content,
                allFriendRequestsByUserId.getTotalElements(),
                allFriendRequestsByUserId.getPageable().getPageNumber(),
                allFriendRequestsByUserId.getTotalPages()
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
        if (Boolean.TRUE.equals(notifiedUser.getIsRead())) {
            throw new BadRequestException(ErrorMessage.NOTIFICATION_ALREADY_READ);
        }
        notifiedUser.setIsRead(true);
        log.info("Set flag isRead: {}", notifiedUser.getIsRead());
        notifiedUserRepo.save(notifiedUser);
        log.info("Successfully update status");
    }
    @Transactional
    public void notifyUsersForEventCanceled(Event event) {
        eventRepo.findUsersByUsersLikedEvents_Id(event.getId())
                .forEach(user -> telegramBotConfig.sendNotificationViaTelegramApi(user.getChatId(),
                        String.format("Unfortunately, event %s was cancelled. %s", event.getTitle(), ZonedDateTime.now())));
    }

    @Transactional
    public void notifyUsersForEventUpdated(Event event) {
        eventRepo.findUsersByUsersLikedEvents_Id(event.getId())
                .forEach(user -> telegramBotConfig.sendNotificationViaTelegramApi(user.getChatId(),
                        String.format("Event %s was updated. New name is %s. %s", event.getTitle(), event.getTitle(), event.getCreationDate())));
    }

    @Override
    @Transactional
    public void createNotificationForEventChanges(UserVO userVO, Long eventId, NotificationSourceType sourceType) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        Notification notification = Notification.builder()
                .title(event.getTitle())
                .creationDate(ZonedDateTime.now())
                .notificationSource(NotificationSource.EVENT)
                .sourceId(event.getId())
                .author(modelMapper.map(userVO, User.class))
                .sourceType(sourceType)
                .build();
        Notification savedNotification = notificationRepo.save(notification);
        log.info("Notification with id {} saved", savedNotification.getId());
        switch (sourceType) {
            case EVENT_CANCELED:
                notifyUsersForEventCanceled(event);
            case EVENT_EDITED:
                //TODO: add all 3 possible variants
                notifyUsersForEventUpdated(event);
        }
    }

    @Override
    @Transactional
    public void delete(Long notificationId, UserVO user) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId));
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(notification.getAuthor().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        notificationRepo.deleteById(notificationId);
    }

    @Override
    @Transactional
    public List<NotificationsDto> findAllUnreadNotificationByUserId(Long userId) {
        return notifiedUserRepo.findAllUnreadNotificationsByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User don't have unread notification"))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createNotificationForEvent(UserVO userVO, EventVO eventVO, NotificationSourceType sourceType) {
        User author = userRepo.findById(eventVO.getOrganizer().getId())
                .orElseThrow(() -> new NotFoundException("Not found"));
        Notification notification = Notification.builder()
                .title(eventVO.getTitle())
                .notificationSource(NotificationSource.EVENT)
                .author(modelMapper.map(userVO, User.class))
                .sourceType(sourceType)
                .sourceId(eventVO.getId())
                .build();
        Notification savedNotification = notificationRepo.save(notification);
        log.info("Notification with id {} was saved", savedNotification.getId());

        NotifiedUser notifiedUser = createNotifiedUser(savedNotification, modelMapper.map(author, UserVO.class));
        NotifiedUser savedNotifiedUser = notifiedUserRepo.save(notifiedUser);
        log.info("Notified user with id {} was saved", savedNotifiedUser.getId());

        switch (sourceType) {
            case EVENT_LIKED:
                telegramBotConfig.sendNotificationViaTelegramApi(author.getChatId(),
                        String.format("%s likes your event: %s", userVO.getName(), eventVO.getTitle()));
                break;
            case EVENT_COMMENTED:
                telegramBotConfig.sendNotificationViaTelegramApi(author.getChatId(),
                        String.format("%s commented on your event %s. Date: %s", userVO.getName(), eventVO.getTitle(), ZonedDateTime.now()));
                break;
        }
    }

    private NotificationsDto convertToDto(NotifiedUser notifiedUser) {
        return NotificationsDto.builder()
                .userName(notifiedUser.getUser().getName())
                .notificationTime(notifiedUser.getNotification().getCreationDate())
                .objectTitle(notifiedUser.getNotification().getTitle())
                .notificationSource(notifiedUser.getNotification().getSourceType().name().toLowerCase())
                .build();
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

        List<NotifiedUser> userNotifications = notifiedUserRepo
                .findByUserIdAndNotificationIdIn(userId, notificationsIds);

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
        User author = modelMapper.map(userService.findById(authorId), User.class);
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
        User author = modelMapper.map(userService.findById(authorId), User.class);
        Notification save = notificationRepo.save(createFriendNotification(author));
        User friend = modelMapper.map(userService.findById(friendId), User.class);
        notifiedUserRepo.save(NotifiedUser.builder()
                .notification(save)
                .user(friend)
                .isRead(false)
                .build());
        if (friend.getChatId() != null) {
            telegramBotConfig.sendNotificationViaTelegramApi(friend.getChatId(),
                    "New friend request from user: " + author.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationDtoResponse findById(Long notificationId) {
        Notification notification =
                notificationRepo.findById(notificationId)
                        .orElseThrow(() ->
                                new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId)
                        );
        return modelMapper.map(notification, NotificationDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<NotificationsDto> getNotificationsForCurrentUser(Long userId, NotificationSourceType sourceType) {
        List<NotifiedUser> allUnreadNotificationsByUserId = notifiedUserRepo
                .findAllUnreadNotificationsByUserId(userId, sourceType);
        List<NotificationsDto> notifications = allUnreadNotificationsByUserId.stream()
                .map(user -> NotificationsDto.builder()
                        .userName(user.getNotification().getAuthor().getName())
                        .objectTitle(user.getNotification().getTitle())
                        .notificationTime(user.getNotification().getCreationDate())
                        .notificationSource(user.getNotification().getNotificationSource().name().toLowerCase())
                        .build())
                .collect(Collectors.toList());
        if (notifications.isEmpty()) {
            throw new NotFoundException("No new notification for current user");
        }
        return notifications;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void createNotification(UserVO userVO, Object sourceVO, NotificationSourceType sourceType) {
        User author = getUserById(userVO.getId());

        String title;
        Long sourceId;
        UserVO sourceAuthor;
        NotificationSource source;
        UserVO parentCommentAuthor;

        if (sourceVO instanceof EcoNewsVO) {
            parentCommentAuthor = null;
            EcoNewsVO ecoNewsVO = (EcoNewsVO) sourceVO;
            title = ecoNewsVO.getTitle();
            sourceId = ecoNewsVO.getId();
            sourceAuthor = ecoNewsVO.getAuthor();
            source = NotificationSource.NEWS;

            if (sourceAuthor.getChatId() != null) {
                if (sourceType.equals(NEWS_LIKED)) {
                    telegramBotConfig.sendNotificationViaTelegramApi(sourceAuthor.getChatId(),
                            "New like for you news: " + ecoNewsVO.getTitle() + "\nFrom user: " + author.getName());
                }
                if (sourceType.equals(NEWS_COMMENTED)) {
                    telegramBotConfig.sendNotificationViaTelegramApi(sourceAuthor.getChatId(),
                            "New comment for your news: " + ecoNewsVO.getTitle() + "\nFrom user: " + author.getName());
                }
            }
        } else if (sourceVO instanceof EcoNewsComment) {
            EcoNewsComment ecoNewsComment = (EcoNewsComment) sourceVO;
            parentCommentAuthor = getParentCommentAuthor(ecoNewsComment);
            title = ecoNewsComment.getEcoNews().getTitle();
            sourceId = ecoNewsComment.getEcoNews().getId();
            sourceAuthor = modelMapper.map(ecoNewsComment.getEcoNews().getAuthor(), UserVO.class);
            source = NotificationSource.NEWS;

            if (sourceAuthor.getChatId() != null) {
                if (sourceType.equals(COMMENT_LIKED)) {
                    telegramBotConfig.sendNotificationViaTelegramApi(sourceAuthor.getChatId(),
                            "New like for you comment: " + ecoNewsComment.getText()
                            + "\nFrom user: " + author.getName());
                }
            }
        } else {
            throw new NotFoundException("Not found source author");
        }

        Notification newNotification = Notification.builder()
                .title(title)
                .sourceId(sourceId)
                .sourceType(sourceType)
                .author(author)
                .creationDate(ZonedDateTime.now())
                .notificationSource(source)
                .build();

        Notification savedNotification = notificationRepo.save(newNotification);
        log.info("Notification with id: {} was saved", savedNotification.getId());

        NotifiedUser notifiedUser = createNotifiedUser(savedNotification, sourceAuthor);
        NotifiedUser savedUser = notifiedUserRepo.save(notifiedUser);
        log.info("Notified user with id {} was saved", savedUser.getId());

        if (parentCommentAuthor != null) {
            NotifiedUser notifiedParentUser = createNotifiedUser(savedNotification, parentCommentAuthor);
            NotifiedUser savedParentUser = notifiedUserRepo.save(notifiedParentUser);
            log.info("Notification for user with id {} saved for parent comment", savedParentUser.getId());

            if (savedParentUser.getUser().getChatId() != null) {
                String newsTitle = ecoNewsRepo.findById(savedNotification.getSourceId())
                        .orElseThrow(() -> new NotFoundException("Eco news with id: "
                                                                 + savedNotification.getSourceId() + " not found"))
                        .getTitle();
                telegramBotConfig.sendNotificationViaTelegramApi(savedParentUser.getUser().getChatId(),
                        "New reply for you comment to news: " + newsTitle + "\nFrom user: " + author.getName());
            }
        }
    }

    private NotifiedUser createNotifiedUser(Notification savedNotification, UserVO userVO) {
        return NotifiedUser.builder()
                .isRead(false)
                .user(getUserById(userVO.getId()))
                .notification(savedNotification)
                .build();
    }

    private User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %d not found", userId)));
    }

    private UserVO getParentCommentAuthor(EcoNewsComment ecoNewsComment) {
        if (ecoNewsComment.getParentComment() != null) {
            return modelMapper.map(ecoNewsComment.getParentComment().getUser(), UserVO.class);
        }
        return null;
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