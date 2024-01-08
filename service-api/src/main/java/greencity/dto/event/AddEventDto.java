package greencity.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddEventDto {
    String title;
    String description;
    boolean open;
    List<EventDateLocationDto> dates;
    List<String> tags;
}
