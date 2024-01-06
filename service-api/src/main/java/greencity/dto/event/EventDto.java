package greencity.dto.event;

import org.springframework.security.core.userdetails.User;

import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.util.List;

public class EventDto {

    private Long id;

    private String title;

    private User organizer;

    private LocalDate creationDate;

    private String description;

    @Max(7)
    private List<EventDateLocationDto> dates;

}
