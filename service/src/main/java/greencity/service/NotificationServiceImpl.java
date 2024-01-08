package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.repository.NotificationRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public List<NotificationDto> getTheLatestThreeNotifications(Long receiverId) {
        return notificationRepo.findTop3ByReceiversIdOrderByCreationDate(receiverId, PageRequest.of(0,3));
    }
}
