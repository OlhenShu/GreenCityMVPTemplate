package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getLanguage;
import static greencity.ModelUtils.getTag;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class TagDtoMapperTest {

    @InjectMocks
    private TagDtoMapper mapper;

    @Test
    void convert() {

        Tag tag = getTag();

        TagTranslation tagTranslation = TagTranslation.builder()
                .id(1L)
                .name("News")
                .language(getLanguage())
                .tag(tag)
                .build();

        TagDto expected = TagDto.builder()
                .id(tagTranslation.getTag().getId())
                .name(tagTranslation.getName())
                .build();

        TagDto actual = mapper.convert(tagTranslation);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }
}