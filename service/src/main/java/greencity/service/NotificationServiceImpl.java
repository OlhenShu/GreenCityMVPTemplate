package greencity.service;

import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final NotificationDtoResponseMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShortNotificationDtoResponse> getTheLatestThreeNotifications(Long receiverId) {
        return notificationRepo.findTop3ByReceiversIdOrderByCreationDate(receiverId, PageRequest.of(0, 3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
