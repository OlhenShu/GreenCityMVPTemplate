package greencity.mapping;

import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getTag;
import static greencity.ModelUtils.getTagVO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class TagVOMapperTest {

    @InjectMocks
    private TagVOMapper mapper;

    @Test
    void convert() {

        Tag tag = getTag();

        TagVO expected = getTagVO();
        TagVO actual = mapper.convert(tag);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getTagTranslations().size(), actual.getTagTranslations().size());
    }
}