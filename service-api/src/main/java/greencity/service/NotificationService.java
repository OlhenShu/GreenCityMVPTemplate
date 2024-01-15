package greencity.service;

import greencity.dto.user.UserVO;

public interface NotificationService {
    /**
     * Method for deleting Notification by its id.
     *
     * @param notificationId Notification id which will be deleted.
     * @param user {@link UserVO}, which notification will be deleted
     */
    void delete(Long notificationId, UserVO user);
}
