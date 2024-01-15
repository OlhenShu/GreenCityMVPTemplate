package greencity.repository;

import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    /**
     * Retrieves the top three notifications for a specific receiver ordered by creation date.
     *
     * @param receiverId The ID of the receiver for whom notifications are retrieved.
     * @return A list of {@link ShortNotificationDtoResponse} objects representing
     *         the top three notifications for the given receiver, ordered by creation date.
     * @author Nikita Malov
     */
    @Query("SELECT new greencity.dto.notification.ShortNotificationDtoResponse(n.id, n.title, nu.isRead) "
        + "FROM Notification n LEFT JOIN n.notifiedUsers nu "
        + "WHERE nu.user.id = :receiverId ORDER BY n.creationDate desc")
    List<ShortNotificationDtoResponse> findTop3ByReceiversIdOrderByCreationDate(Long receiverId, Pageable pageable);
    /**
     * Method that returns page of {@link NotificationDtoResponse} received by user with specified id.
     *
     * @param userId    user id.
     * @param page      {@link Pageable} object.
     * @return          page of {@link NotificationDtoResponse}.
     */
    @Query("SELECT new greencity.dto.notification.NotificationDtoResponse("
        + "n.id, n.author.id, n.author.name, n.title, n.sourceType, n.sourceId, nu.isRead, n.creationDate) "
        + "FROM Notification n LEFT JOIN n.notifiedUsers nu "
        + "WHERE nu.user.id = :userId ORDER BY n.creationDate DESC")
    Page<NotificationDtoResponse> findAllReceivedNotificationDtoByUserId(Long userId, Pageable page);

    @Query("SELECT new greencity.dto.notification.NotificationDtoResponse("
            + "n.id, n.author.id, n.author.name, n.title, n.sourceType, n.sourceId, nu.isRead, n.creationDate) "
            + "FROM Notification n LEFT JOIN n.notifiedUsers nu "
            + "WHERE nu.user.id = :userId AND n.sourceType = 'FRIEND_REQUEST' ORDER BY n.creationDate DESC")
    Page<NotificationDtoResponse> findAllFriendRequestsByUserId(Long userId, Pageable page);

}