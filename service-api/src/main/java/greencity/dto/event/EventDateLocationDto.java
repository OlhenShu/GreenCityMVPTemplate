package greencity.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDateLocationDto {
    Long id;
    AddressDto coordinates;
    EventDto event;
    ZonedDateTime finishDate;
    ZonedDateTime startDate;
    String onlineLink;
}
