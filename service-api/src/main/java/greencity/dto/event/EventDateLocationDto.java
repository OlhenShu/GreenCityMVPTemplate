package greencity.dto.event;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventDateLocationDto {
    private Long id;
    private Long eventId;
    private ZonedDateTime startDate;
    private ZonedDateTime finishDate;
    private CoordinatesDto coordinates;
}
