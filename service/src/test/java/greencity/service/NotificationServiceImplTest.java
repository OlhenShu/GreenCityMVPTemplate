package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationSourceType;
import greencity.repository.NotificationRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationRepo notificationRepo;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    public void findAllByUserTest() {
        List<NotificationDtoResponse> notificationDtoList = new ArrayList<>();
        notificationDtoList.add(
            new NotificationDtoResponse(2L, 2L, "name1", "title",
                NotificationSourceType.NEWS_LIKED, 1L, false, ZonedDateTime.now()));
        notificationDtoList.add(
            new NotificationDtoResponse(3L, 1L, "name2", "title",
                NotificationSourceType.NEWS_COMMENTED, 23L, true, ZonedDateTime.now()));
        Page<NotificationDtoResponse> notificationDtoPage = new PageImpl<>(notificationDtoList, PageRequest.of(0, 10), 2L);

        when(notificationRepo.findAllReceivedNotificationDtoByUserId(anyLong(), any(Pageable.class)))
            .thenReturn(notificationDtoPage);

        PageableDto<NotificationDtoResponse> pageableDto = notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10));
        PageableDto<NotificationDtoResponse> expectedPageableDto = new PageableDto<>(notificationDtoList, 2L, 0, 1);

        verify(notificationRepo).findAllReceivedNotificationDtoByUserId(userVO.getId(), PageRequest.of(0, 10));
        assertEquals(expectedPageableDto, pageableDto);
    }
}
