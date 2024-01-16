package greencity.dto.search;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;


@Getter
@Builder
@EqualsAndHashCode
public class SearchResponseDto {
    private final List<SearchNewsDto> ecoNews;
    private final Long countOfResults;

    /**
     * Builder for creating SearchResponseDto.java class
     * @param ecoNews all new that is found for user request.
     * @param countOfResults counter of results.
     * @return SearchResponseDto
     */
    public static SearchResponseDto create(List<SearchNewsDto> ecoNews, Long countOfResults) {
        return builder()
                .ecoNews(ecoNews)
                .countOfResults(countOfResults)
                .build();
    }
}
