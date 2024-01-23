package greencity.dto.notification;

import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserForListDto;
import greencity.enums.NotificationSourceType;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationDtoRequest {
    private AuthorDto author;
    private String title;
    private ZonedDateTime creationDate;
    private NotificationSourceType sourceType;
    private Set<UserForListDto> receivers;
    private Long sourceId;
}
