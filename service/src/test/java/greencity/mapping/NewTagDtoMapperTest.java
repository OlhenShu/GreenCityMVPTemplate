package greencity.mapping;

import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getTag;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NewTagDtoMapperTest {

    private final NewTagDtoMapper mapper = new NewTagDtoMapper();

    @Test
    void convert() {

        Tag tag = getTag();
        NewTagDto expected = NewTagDto.builder()
                .id(tag.getId())
                .name(tag.getTagTranslations().get(1).getName())
                .nameUa(tag.getTagTranslations().get(0).getName())
                .build();

        NewTagDto actual = mapper.convert(tag);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getNameUa(), actual.getNameUa());
    }
}