package greencity.repository;

import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.enums.NotificationSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotifiedUserRepo extends JpaRepository<NotifiedUser, Long> {
    /**
     * Retrieves a notified user by the specified user ID and notification ID.
     *
     * @param userId         The ID of the user.
     * @param notificationId The ID of the notification.
     * @return An Optional containing the notified user if found, otherwise empty.
     */
    Optional<NotifiedUser> findByUserIdAndNotificationId(Long userId, Long notificationId);

    /**
     * Retrieves the top 3 unread notifications for a specific user.
     *
     * @param userId The ID of the user for whom to retrieve notifications.
     * @return List of the top 3 unread notifications for the specified user, ordered by creation date.
     */
    @Query("SELECT nu FROM NotifiedUser nu "
            + "JOIN FETCH nu.notification n "
            + "WHERE nu.user.id = :userId AND nu.isRead = false "
            + "ORDER BY nu.notification.creationDate DESC")
    List<Notification> findTop3UnreadNotificationsForUser(@Param("userId") Long userId);

    /**
     * Retrieves a list of NotifiedUser entities for a specific user and a list of notification IDs.
     *
     * @param userId          The ID of the user for whom to retrieve NotifiedUser entities.
     * @param notificationIds List of notification IDs for which to retrieve NotifiedUser entities.
     * @return List of NotifiedUser entities corresponding to the specified user and notification IDs.
     */
    List<NotifiedUser> findByUserIdAndNotificationIdIn(Long userId, List<Long> notificationIds);

    /**
     * Retrieves all unread notifications for the specified user and notification source type.
     *
     * @param userId     The ID of the user to fetch notifications for.
     * @param sourceType The source type of the notifications.
     * @return List of {@link NotifiedUser} representing unread notifications for the specified user and source type.
     */
    @Query("SELECT nu FROM NotifiedUser nu "
            + "JOIN FETCH nu.notification n "
            + "WHERE nu.user.id = :userId "
            + "AND nu.isRead = false "
            + "AND n.sourceType = :sourceType "
            + "ORDER BY n.creationDate DESC")
    List<NotifiedUser> findAllUnreadNotificationsByUserId(@Param("userId") Long userId,
                                                          @Param("sourceType") NotificationSourceType sourceType);

    /**
    * Retrieves a list of unread notifications for a specific user.
    * This method performs a query to fetch NotifiedUser entities with associated unread notifications
    * for the specified user, ordered by the creation date of the notifications in descending order.
    *
    * @param userId the unique identifier of the user for whom unread notifications are being retrieved
    * @return An Optional containing a List of NotifiedUser entities with associated unread notifications.
    */
    @Query("SELECT nu FROM NotifiedUser nu "
           + "JOIN FETCH nu.notification n "
           + "WHERE nu.user.id = :userId "
           + "AND nu.isRead = false "
           + "ORDER BY n.creationDate DESC")
    Optional<List<NotifiedUser>> findAllUnreadNotificationsByUserId(@Param("userId") Long userId);
}