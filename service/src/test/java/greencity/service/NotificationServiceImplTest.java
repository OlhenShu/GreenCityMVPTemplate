package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
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
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        notificationDtoList.add(
            new NotificationDto(2L, 2L, "name1", "title", "shortDescription", true, ZonedDateTime.now()));
        notificationDtoList.add(
            new NotificationDto(3L, 3L, "name2", "title", "shortDescription", true, ZonedDateTime.now()));
        Page<NotificationDto> notificationDtoPage = new PageImpl<>(notificationDtoList, PageRequest.of(0, 10), 2L);

        when(notificationRepo.findAllReceivedNotificationDtoByUserId(anyLong(), any(Pageable.class)))
            .thenReturn(notificationDtoPage);

        PageableDto<NotificationDto> pageableDto = notificationService.findAllByUser(userVO.getId(), PageRequest.of(0, 10));
        PageableDto<NotificationDto> expectedPageableDto = new PageableDto<>(notificationDtoList, 2L, 0, 1);

        verify(notificationRepo).findAllReceivedNotificationDtoByUserId(userVO.getId(), PageRequest.of(0, 10));
        assertEquals(expectedPageableDto, pageableDto);
    }
}
