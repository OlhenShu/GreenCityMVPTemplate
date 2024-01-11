package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotifiedUserRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotifiedUserServiceImpl implements NotifiedUserService {
    private final NotifiedUserRepo notifiedUserRepo;
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;


    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsReadNotification(Long userId, Long notificationId) {
        NotifiedUser notifiedUser = notifiedUserRepo.findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new NotFoundException("Notified user or notification not found"));
        if (notifiedUser.getIsRead()) {
            throw new BadRequestException("Notification already read");
        }
        notifiedUser.setIsRead(true);
        log.info("Set flag isRead: {}", notifiedUser.getIsRead());
        notifiedUserRepo.save(notifiedUser);
        log.info("Successfully update status");
    }

    @Override
    public void notifyUser(Long userId, NotificationDtoResponse notificationDtoResponse) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        NotifiedUser notifiedUser = NotifiedUser.builder()
                .id(notificationDtoResponse.getId())
                .user(user)
                .notification(objectMapper.convertValue(notificationDtoResponse, Notification.class))
                .isRead(false)
                .build();
        notifiedUserRepo.save(notifiedUser);

    }
}
