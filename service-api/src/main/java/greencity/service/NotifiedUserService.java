package greencity.service;

public interface NotifiedUserService {
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
}
