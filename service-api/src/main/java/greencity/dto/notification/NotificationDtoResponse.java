package greencity.dto.notification;

import greencity.dto.user.AuthorDto;
import greencity.enums.NotificationSourceType;
import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationDtoResponse {
    private AuthorDto author;
    private String title;
    private ZonedDateTime creationDate;
    private NotificationSourceType sourceType;
    private Boolean isRead;
    private Long sourceId;
}
