package greencity.mapping;

import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getTag;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class NewTagDtoMapperTest {
    @InjectMocks
    private NewTagDtoMapper mapper;

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