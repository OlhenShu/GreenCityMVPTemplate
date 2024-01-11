package greencity.service;

import greencity.dto.notification.NotificationDtoResponse;

public interface NotifiedUserService {
    /**
     * Marks a notification as read for the specified user.
     *
     * @param userId         The ID of the user.
     * @param notificationId The ID of the notification to mark as read.
     */
    void markAsReadNotification(Long userId, Long notificationId);
    void notifyUser(Long userId, NotificationDtoResponse notificationDtoRequest);
}
