package greencity.service;

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
     *         three notifications for the given receiver.
     * @author Nikita Malov
     */
    List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId);
}
