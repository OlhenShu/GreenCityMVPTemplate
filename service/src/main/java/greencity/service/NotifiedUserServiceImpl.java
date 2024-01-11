package greencity.service;

import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotifiedUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotifiedUserServiceImpl implements NotifiedUserService {
    private final NotifiedUserRepo notifiedUserRepo;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void readLatestNotification(Long userId) {
        List<Notification> unreadNotificationsForUser = notifiedUserRepo.findTop3UnreadNotificationsForUser(userId);
        if (unreadNotificationsForUser.isEmpty()) {
            throw new NotFoundException("Not found unread notifications for current user");
        }

        List<Long> notificationsIds = unreadNotificationsForUser.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());

        List<NotifiedUser> notifiedUsersToUpdate = notifiedUserRepo.findByUserIdAndNotificationIdIn(userId, notificationsIds);

        notifiedUsersToUpdate.forEach(notifiedUser -> notifiedUser.setIsRead(true));

        notifiedUserRepo.saveAll(notifiedUsersToUpdate);
        log.info("Updated statuses for latest 3 unread notification");
    }
}