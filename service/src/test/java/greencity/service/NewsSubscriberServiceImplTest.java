package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.NewsSubscriberRepo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewsSubscriberServiceImplTest {
    @Mock
    NewsSubscriberRepo newsSubscriberRepo;

    @InjectMocks
    NewsSubscriberServiceImpl newsSubscriberService;


    @Test
    public void saveTest(){
        String email = ModelUtils.TEST_EMAIL;
        when(newsSubscriberRepo.existsByEmail(email)).thenReturn(false);


        newsSubscriberService.save(email);
        verify(newsSubscriberRepo).existsByEmail(email);
        verify(newsSubscriberRepo).save(any(NewsSubscriber.class));
    }

    @Test
    public void saveThrowsExceptionWithNotExistingEmailTest(){
        String email = ModelUtils.TEST_EMAIL_2;
        when(newsSubscriberRepo.existsByEmail(email)).thenReturn(true);

        assertThrows(BadRequestException.class,
            () -> newsSubscriberService.save(email),
            ErrorMessage.NEWS_SUBSCRIBER_EXIST);
        verify(newsSubscriberRepo).existsByEmail(email);
    }
}
