package greencity.dto.notification;

import greencity.dto.user.AuthorDto;
import java.time.ZonedDateTime;
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

    private String title;

    private String shortDescription;

    private Boolean isRead = false;

    private ZonedDateTime creationDate;

    /**
     * Constructor with parameters.
     * @param id                id of the notification.
     * @param title             title of the notification.
     * @param isRead            is notification read.
     */
    public NotificationDto(Long id,String title, Boolean isRead) {
        this.id = id;
        this.title = title;
        this.isRead = isRead;
    }
}
