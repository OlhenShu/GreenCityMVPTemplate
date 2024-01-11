package greencity.service;

import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;

import java.util.List;


/**
 * Service handling operations related to notifications.
 */
public interface NotificationService {
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
     * Sends a friend request to the specified user with
     *
     * @param authorId the ID of the author for the notification
     * @param friendId  the object containing data to create the notification
     * @author Klopov Dmytro
     */
    void friendRequestNotification(Long authorId, Long friendId);

    NotificationDtoResponse findById(Long notificationId);
}
