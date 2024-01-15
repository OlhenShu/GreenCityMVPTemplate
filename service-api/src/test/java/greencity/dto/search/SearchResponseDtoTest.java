package greencity.dto.search;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static greencity.ModelUtils.getAddEcoNewsDtoResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchResponseDtoTest {

    @Test
    void create() {
        SearchNewsDto searchNewsDto = SearchNewsDto.builder()
                .creationDate(ZonedDateTime.now())
                .title("title")
                .tags(List.of())
                .author(getAddEcoNewsDtoResponse().getEcoNewsAuthorDto())
                .build();

        SearchResponseDto responseDto = SearchResponseDto.builder()
                .ecoNews(List.of(searchNewsDto))
                .countOfResults(1L)
                .build();

        assertEquals(SearchResponseDto.create(List.of(searchNewsDto), 1L), responseDto);
    }
}