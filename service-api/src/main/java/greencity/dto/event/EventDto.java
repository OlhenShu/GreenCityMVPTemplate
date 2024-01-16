package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
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
