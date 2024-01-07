package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDto;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    /**
     * Method that returns page of {@link NotificationDto} received by user with specified id.
     *
     * @param userId    user id.
     * @param page      {@link Pageable} object.
     * @return          page of {@link NotificationDto}.
     */
    PageableDto<NotificationDto> findAllByUser(Long userId, Pageable page);
}
