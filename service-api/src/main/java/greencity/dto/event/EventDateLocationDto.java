package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import greencity.dto.geocoding.AddressDto;
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
    @JsonIgnore
    EventDto event;
    ZonedDateTime finishDate;
    ZonedDateTime startDate;
    String onlineLink;
}
