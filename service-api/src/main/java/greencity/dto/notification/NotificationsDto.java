package greencity.dto.notification;

import lombok.*;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationsDto {
    private String userName;
    private String objectTitle;
    private ZonedDateTime notificationTime;
}