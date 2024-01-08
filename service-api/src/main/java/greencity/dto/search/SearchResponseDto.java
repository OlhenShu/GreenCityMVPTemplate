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


    // Make the constructor private to enforce the use of the builder pattern

    /**
     * Constructor.
     * @param ecoNews constructor parameter.
     * @param countOfResults constructor parameter.
     */
    private SearchResponseDto(List<SearchNewsDto> ecoNews, Long countOfResults) {
        this.ecoNews = ecoNews;
        this.countOfResults = countOfResults;
    }

    // Add a public static method to create an instance using the builder

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
