package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventDateLocationDto {
    private Long id;
    private AddressDto coordinates;
    @JsonIgnore
    private EventDto event;
    private ZonedDateTime startDate;
    private ZonedDateTime finishDate;
    private String onlineLink;
}
