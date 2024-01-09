package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<NotificationDtoResponse> findAllByUser(Long userId, Pageable page) {
        Page<NotificationDtoResponse> notificationDtoPage = notificationRepo
            .findAllReceivedNotificationDtoByUserId(userId, page);
        return new PageableDto<>(
            notificationDtoPage.getContent(),
            notificationDtoPage.getTotalElements(),
            notificationDtoPage.getPageable().getPageNumber(),
            notificationDtoPage.getTotalPages()
        );
    }
}
