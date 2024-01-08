package greencity.service;

import greencity.dto.notification.NotificationDto;
import java.util.List;


/**
 * Service handling operations related to notifications.
 */
public interface NotificationService {
    /**
     * Retrieves the latest three notifications for a specific receiver as NotificationDto objects.
     *
     * @param receiverId The ID of the receiver for whom notifications are retrieved.
     * @return A list of NotificationDto objects representing the latest three notifications for the given receiver.
     * @author Nikita Malov
     */
    List<NotificationDto> getTheLatestThreeNotifications(Long receiverId);
}
