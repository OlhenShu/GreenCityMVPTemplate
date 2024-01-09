package greencity.repository;

import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
