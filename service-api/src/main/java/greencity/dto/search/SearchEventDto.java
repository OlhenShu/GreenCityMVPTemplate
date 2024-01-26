package greencity.dto.search;

import greencity.dto.event.EventAuthorDto;
import greencity.dto.user.EcoNewsAuthorDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchEventDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private EventAuthorDto organizer;
    @NotEmpty
    private LocalDate creationDate;
    @NotEmpty
    private List<String> tags;
}
