package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.NewsSubscriberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsSubscriberServiceImpl implements NewsSubscriberService {
    private final NewsSubscriberRepo newsSubscriberRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void save(String email) {
        if (newsSubscriberRepo.existsByEmail(email)) {
            throw new BadRequestException(ErrorMessage.NEWS_SUBSCRIBER_EXIST);
        }
        newsSubscriberRepo.save(NewsSubscriber.builder().email(email).build());
    }
}
