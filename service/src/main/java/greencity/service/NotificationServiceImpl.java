package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import greencity.repository.NotifiedUserRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotifiedUserRepo notifiedUserRepo;
    private final UserRepo userRepo;
    private final NotificationDtoResponseMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId) {
        return notificationRepo.findTop3ByReceiversIdOrderByCreationDate(receiverId, PageRequest.of(0, 3));
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public NotificationDtoResponse createNewNotification(Long authorId, NewNotificationDtoRequest request) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User with current id not found"));
        Notification newNotification = Notification.builder()
                .creationDate(ZonedDateTime.now())
                .title(request.getTitle())
                .author(author)
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .build();
        Notification savedNotification = notificationRepo.save(newNotification);
        log.info("Notification with id {} saved", savedNotification.getId());
        return mapper.convert(savedNotification);
    }
}