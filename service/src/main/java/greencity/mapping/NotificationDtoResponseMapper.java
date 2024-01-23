package greencity.mapping;

import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoResponseMapper extends AbstractConverter<Notification, NotificationDtoResponse> {
    @Override
    public NotificationDtoResponse convert(Notification notification) {
        return NotificationDtoResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .author(AuthorDto.builder()
                        .id(notification.getAuthor().getId())
                        .name(notification.getAuthor().getName())
                        .build())
                .creationDate(notification.getCreationDate())
                .isRead(false)
                .sourceId(notification.getSourceId())
                .sourceType(notification.getSourceType())
                .build();
    }
}
