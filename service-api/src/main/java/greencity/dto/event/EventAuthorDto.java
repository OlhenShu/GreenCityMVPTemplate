package greencity.dto.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventAuthorDto {
    Long id;
    String name;
    double organizerRating;
}
