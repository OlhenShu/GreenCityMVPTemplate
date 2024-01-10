package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
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
    private String titleImage;
    @Max(7)
    private List<EventDateLocationDto> dates;
    private boolean open;
    private boolean isFavorite;
    private boolean isSubscribed;
    private List<String> additionalImages;
    private List<TagUaEnDto> tags;
}
