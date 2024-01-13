package greencity.dto.notification;

import greencity.enums.NotificationSourceType;
import lombok.*;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class NewNotificationDtoRequest {
    private String title;
    private ZonedDateTime creationDate;
    private NotificationSourceType sourceType;
    private Long sourceId;
}
