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
    private SearchResponseDto(List<SearchNewsDto> ecoNews, Long countOfResults) {
        this.ecoNews = ecoNews;
        this.countOfResults = countOfResults;
    }

    // Add a public static method to create an instance using the builder
    public static SearchResponseDto create(List<SearchNewsDto> ecoNews, Long countOfResults) {
        return builder()
                .ecoNews(ecoNews)
                .countOfResults(countOfResults)
                .build();
    }
}
