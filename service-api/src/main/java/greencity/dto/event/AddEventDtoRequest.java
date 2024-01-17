package greencity.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddEventDtoRequest {
    String title;
    String description;
    boolean open;
    List<EventDateLocationDto> datesLocations;
    List<String> tags;
}
