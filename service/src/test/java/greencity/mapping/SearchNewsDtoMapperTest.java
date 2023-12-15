package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getEcoNews;
import static greencity.ModelUtils.getSearchNewsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchNewsDtoMapperTest {

    private final SearchNewsDtoMapper mapper = new SearchNewsDtoMapper();

    @Test
    void convert() {

        EcoNews ecoNews = getEcoNews();
        SearchNewsDto expected = getSearchNewsDto();
        SearchNewsDto actual = mapper.convert(ecoNews);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAuthor().getName(), actual.getAuthor().getName());
        assertEquals(expected.getTags().size(), actual.getTags().size());
        assertEquals(expected.getTitle(), actual.getTitle());
    }
}