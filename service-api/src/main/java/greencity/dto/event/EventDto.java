package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventDto {
    Long id;
    String creationDate;
    String description;
    String title;
    String titleImage;
    List<String> additionalImages;
    boolean isFavorite;
    boolean isSubscribed;
    boolean open;
    List<EventDateLocationDto> dates;
    EventAuthorDto organizer;
    List<TagUaEnDto> tags;
}
