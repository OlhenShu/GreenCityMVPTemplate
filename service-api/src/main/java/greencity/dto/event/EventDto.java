package greencity.dto.event;

import lombok.*;

import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    private String title;
    private EventAuthorDto organizer;
    private LocalDate creationDate;
    private String description;
    @Max(7)
    private List<EventDateLocationDto> dates;
}
