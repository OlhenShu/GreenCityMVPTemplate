package greencity.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class SearchResponseDto {
    private final List<SearchNewsDto> ecoNews;
    private final List<SearchEventDto> events;
    private final Long countOfEcoNews;
    private final Long countOfEvents;
}
