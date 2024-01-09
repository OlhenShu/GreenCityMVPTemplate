package greencity.repository;

import greencity.entity.NotifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;

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
}