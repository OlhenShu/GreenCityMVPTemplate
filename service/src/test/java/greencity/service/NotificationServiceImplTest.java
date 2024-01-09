package greencity.service;

import greencity.ModelUtils;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.repository.NotificationRepo;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    public void getTheLatestThreeNotifications() {
       var expected = List.of(
           new ShortNotificationDtoResponse(1L,"title", true),
           new ShortNotificationDtoResponse(2L,"title", true)
       );

        when(notificationRepo.findTop3ByReceiversIdOrderByCreationDate(anyLong(), any(Pageable.class)))
            .thenReturn(expected);

        var actual = notificationService.getTheLatestThreeNotifications(userVO.getId());

        verify(notificationRepo).findTop3ByReceiversIdOrderByCreationDate(userVO.getId(), PageRequest.of(0, 3));
        assertEquals(expected, actual);
    }
}
