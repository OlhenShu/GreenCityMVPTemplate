package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;

    @Override
    @Transactional
    public void delete(Long notificationId, UserVO user) {
        Notification notification = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId));
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(notification.getAuthor().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        notificationRepo.deleteById(notificationId);
    }
}
