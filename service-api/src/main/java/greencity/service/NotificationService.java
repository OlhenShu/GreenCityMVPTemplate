package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.NotificationsForEcoNewsDto;
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
     * The list is paginated based on the provided {@code page} parameter.
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
     * @return A list of {@link ShortNotificationDtoResponse} objects representing the latest
     * three notifications for the given receiver.
     * @author Nikita Malov
     */
    List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId);

    /**
     * Creates a new notification based on the provided data in the request and returns the corresponding notification DTO.
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
     * Sends a friend request to the specified user with
     *
     * @param authorId the ID of the author for the notification
     * @param friendId the object containing data to create the notification
     * @author Klopov Dmytro
     */
    void friendRequestNotification(Long authorId, Long friendId);

    NotificationDtoResponse findById(Long notificationId);

    /**
     * Retrieves the notifications related to EcoNews for the specified user.
     *
     * @param userId The ID of the user to fetch notifications for.
     * @return List of {@link NotificationsForEcoNewsDto} representing notifications for EcoNews.
     * @author Kizerov Dmytro
     */
    List<NotificationsForEcoNewsDto> getNotificationsEcoNewsForCurrentUser(Long userId);

    /**
     * Creates and saves an EcoNews notification for the specified user based on the provided EcoNews data and source type.
     *
     * @param userVO     The authenticated user who will receive the notification.
     * @param ecoNewsVO  The EcoNews data associated with the notification.
     * @param sourceType The source type of the notification.
     * @author Kizerov Dmytro
     */
    void createEcoNewsNotification(UserVO userVO, EcoNewsVO ecoNewsVO, NotificationSourceType sourceType);
}