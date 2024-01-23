package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.entity.Notification;
import greencity.entity.NotifiedUser;
import greencity.entity.User;
import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationDtoResponseMapperTest {
    @InjectMocks
    private NotificationDtoResponseMapper mapper;

    @Test
    void convert() {
        Notification notification = ModelUtils.getNotification();

        NotificationDtoResponse expected = NotificationDtoResponse.builder()
                .id(notification.getId())
                .author(AuthorDto.builder()
                        .id(notification.getAuthor().getId())
                        .name(notification.getAuthor().getName())
                        .build())
                .title(notification.getTitle())
                .creationDate(notification.getCreationDate())
                .sourceType(notification.getSourceType())
                .isRead(false)
                .sourceId(notification.getSourceId())
                .build();

        assertEquals(expected, mapper.convert(notification));

    }
}
