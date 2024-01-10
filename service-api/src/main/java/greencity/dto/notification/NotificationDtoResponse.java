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
    private Long id;
    private AuthorDto author;
    private String title;
    private ZonedDateTime creationDate;
    private NotificationSourceType sourceType;
    private Boolean isRead;
    private Long sourceId;

    /**
     * Constructs a Notification Data Transfer Object (DTO) Response.
     *
     * @param id            The unique identifier for the notification.
     * @param authorId      The unique identifier of the author associated with the notification.
     * @param authorName    The name of the author associated with the notification.
     * @param title         The title or subject of the notification.
     * @param sourceType    The type of the source generating the notification (e.g., message, event).
     * @param sourceId      The unique identifier of the source related to the notification.
     * @param isRead        A boolean indicating whether the notification has been read (true) or not (false).
     * @param creationDate  The date and time when the notification was created.
     */
    public NotificationDtoResponse(Long id, Long authorId, String authorName, String title,
                                   NotificationSourceType sourceType, Long sourceId, Boolean isRead,
                                   ZonedDateTime creationDate) {
        this.id = id;
        this.author = new AuthorDto(authorId, authorName);
        this.title = title;
        this.creationDate = creationDate;
        this.sourceType = sourceType;
        this.isRead = isRead;
        this.sourceId = sourceId;
    }
}
