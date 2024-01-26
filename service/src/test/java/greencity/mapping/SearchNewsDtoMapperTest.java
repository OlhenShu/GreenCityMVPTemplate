package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getEcoNews;
import static greencity.ModelUtils.getSearchNewsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class SearchNewsDtoMapperTest {
    @InjectMocks
    private SearchNewsDtoMapper mapper;

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