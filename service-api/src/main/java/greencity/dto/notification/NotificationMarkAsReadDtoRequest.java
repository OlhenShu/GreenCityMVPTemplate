package greencity.dto.notification;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class NotificationMarkAsReadDtoRequest {

    private Long notificationId;
}
