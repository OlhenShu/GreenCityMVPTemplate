package greencity.dto.event;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventDtoForSubscribedUser {
    private String eventTitle;
    private LocalDate creationDate;
}
