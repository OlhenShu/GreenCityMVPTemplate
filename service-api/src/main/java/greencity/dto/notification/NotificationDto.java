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

    /**
     * Constructor with parameters.
     * @param id                id of the notification.
     * @param authorId          id of the author.
     * @param authorName        name of the author.
     * @param title             title of the notification.
     * @param shortDescription  short description of the notification.
     * @param isRead            is notification read.
     * @param creationDate      creation date of the notification.
     */
    public NotificationDto(Long id, Long authorId, String authorName, String title, String shortDescription,
                           Boolean isRead, ZonedDateTime creationDate) {
        this.id = id;
        this.author = new AuthorDto(authorId, authorName);
        this.title = title;
        this.shortDescription = shortDescription;
        this.isRead = isRead;
        this.creationDate = creationDate;
    }
}
