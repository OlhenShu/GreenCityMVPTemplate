package greencity.dto.event;

import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventDateLocationDto {
    Long id;
    AddressDto coordinates;
    EventDto event;
    ZonedDateTime finishDate;
    ZonedDateTime startDate;
    String onlineLink;
}
