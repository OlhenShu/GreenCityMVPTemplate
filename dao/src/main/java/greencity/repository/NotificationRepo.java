package greencity.repository;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    /**
     * Method that returns page of {@link NotificationDto} received by user with specified id.
     *
     * @param userId    user id.
     * @param page      {@link Pageable} object.
     * @return          page of {@link NotificationDto}.
     */
    @Query("SELECT new greencity.dto.notification.NotificationDto("
        + "n.id, n.author.id, n.author.name, n.title, n.shortDescription, n.isRead, n.creationDate) "
        + "FROM Notification n LEFT JOIN n.receivers r "
        + "WHERE r.id = :userId")
    Page<NotificationDto> findAllReceivedNotificationDtoByUserId(Long userId, Pageable page);
}
