package greencity.mapping;

import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getFactTranslation;
import static greencity.ModelUtils.getLanguageTranslationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class LanguageTranslationDtoMapperTest {
    @InjectMocks
    private LanguageTranslationDtoMapper mapper;

    @Test
    void convert() {

        HabitFactTranslation habitFactTranslation = getFactTranslation();

        LanguageTranslationDTO expected = getLanguageTranslationDTO();
        LanguageTranslationDTO actual = mapper.convert(habitFactTranslation);

        assertEquals(expected.getLanguage().getCode(), actual.getLanguage().getCode());
        assertEquals(expected.getLanguage().getId(), actual.getLanguage().getId());
    }
}