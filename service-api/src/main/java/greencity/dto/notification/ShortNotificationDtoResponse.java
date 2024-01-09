package greencity.dto.notification;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ShortNotificationDtoResponse {
    private Long id;
    private String title;
    private Boolean isRead;
}
