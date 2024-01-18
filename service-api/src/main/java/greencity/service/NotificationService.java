package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsDto;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationSourceType;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service handling operations related to notifications.
 */
public interface NotificationService {
    /**
     * Method that returns page of {@link NotificationDtoResponse} received by user with specified id.
     *
     * @param userId user id.
     * @param page   {@link Pageable} object.
     * @return page of {@link NotificationDtoResponse}.
     */
    PageableDto<NotificationDtoResponse> findAllByUser(Long userId, Pageable page);

    /**
     * Retrieves a pageable list of friend requests for a given user.
     * This method fetches a paginated list of friend requests received by the specified user.
     *
     * @param userId The unique identifier of the user for whom friend requests are to be retrieved.
     * @param page   The pagination information to determine the page number, size, sorting, etc.
     * @return A {@link PageableDto} containing {@link NotificationDtoResponse} objects representing friend requests.
     * @author Dmytro Klopov
     * @see NotificationDtoResponse
     * @see PageableDto
     * @see Pageable
     */
    PageableDto<NotificationDtoResponse> findAllFriendRequestsByUserId(Long userId, Pageable page);

    /**
     * Retrieves the latest three notifications for a specific receiver as {@link ShortNotificationDtoResponse} objects.
     *
     * @param receiverId The ID of the receiver for whom notifications are retrieved.
     * @return A list of {@link ShortNotificationDtoResponse} objects representing the latest three notifications
     * @author Nikita Malov
     */
    List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId);

    /**
     * Creates a new notification based on the provided data in the request and returns the corresponding notification.
     *
     * @param authorId the ID of the author for the notification
     * @param request  the object containing data to create the notification
     * @return the DTO representing the notification {@link NotificationDtoResponse}
     * @author Kizerov Dmytro
     */
    NotificationDtoResponse createNewNotification(Long authorId, NewNotificationDtoRequest request);

    /**
     * Marks a notification as read for the specified user.
     *
     * @param userId         The ID of the user.
     * @param notificationId The ID of the notification to mark as read.
     */
    void markAsReadNotification(Long userId, Long notificationId);

    /**
     * Marks the latest unread notifications as read for the specified user.
     *
     * @param userId The ID of the user for whom to mark the latest unread notifications as read.
     */
    void readLatestNotification(Long userId);

    /**
     * Sends a friend request to the specified user.
     *
     * @param authorId the ID of the author for the notification
     * @param friendId the object containing data to create the notification
     * @author Klopov Dmytro
     */
    void friendRequestNotification(Long authorId, Long friendId);

    /**
     * Retrieves a notification by its unique identifier.
     * This method is designed to fetch a specific notification based on the provided notification ID.
     *
     * @param notificationId The unique identifier of the notification to be retrieved.
     * @return A NotificationDtoResponse object representing the retrieved notification.
     * @see greencity.dto.notification.NotificationDtoResponse
     */
    NotificationDtoResponse findById(Long notificationId);

    /**
     * Retrieves the notifications related to EcoNews for the specified user.
     *
     * @param userId     The ID of the user to fetch notifications for.
     * @param sourceType The type of the notification source.
     * @return List of {@link NotificationsDto} representing notifications for EcoNews.
     * @author Kizerov Dmytro
     */
    List<NotificationsDto> getNotificationsForCurrentUser(Long userId, NotificationSourceType sourceType);

    /**
     * Creates a notification for the specified user based on the provided source and source type.
     *
     * @param userVO     The UserVO for whom the notification is created.
     * @param source     The source object (e.g., EcoNewsVO, EcoNewsComment) providing information for the notification.
     * @param sourceType The type of the notification source.
     */
    void createNotification(UserVO userVO, Object source, NotificationSourceType sourceType);
}