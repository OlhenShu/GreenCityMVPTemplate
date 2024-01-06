package greencity.dto.notification;

import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserForListDto;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationDto {
    private Long id;

    private AuthorDto author;

    private Set<UserForListDto> receivers;

    private String title;

    private String shortDescription;

    private Boolean isRead = false;

    private ZonedDateTime creationDate;
}
