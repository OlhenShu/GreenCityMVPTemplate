package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
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
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<NotificationDto> findAllByUser(Long userId, Pageable page) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        Page<NotificationDto> notificationDtoPage = notificationRepo
            .findAllReceivedNotificationDtoByUserId(userId, page);
        return new PageableDto<>(
            notificationDtoPage.getContent(),
            notificationDtoPage.getTotalElements(),
            notificationDtoPage.getPageable().getPageNumber(),
            notificationDtoPage.getTotalPages()
        );
    }
}
