package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    /**
     * Method that returns page of {@link NotificationDtoResponse} received by user with specified id.
     *
     * @param userId    user id.
     * @param page      {@link Pageable} object.
     * @return          page of {@link NotificationDtoResponse}.
     */
    PageableDto<NotificationDtoResponse> findAllByUser(Long userId, Pageable page);
}
