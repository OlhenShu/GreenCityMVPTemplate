package greencity.dto.notification;

import lombok.*;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class LikesNotificationDto {
    private String userName;
    private String title;
    private ZonedDateTime notificationTime;
}