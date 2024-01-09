package greencity.repository;

import greencity.dto.notification.NotificationDtoResponse;
import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
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
}
