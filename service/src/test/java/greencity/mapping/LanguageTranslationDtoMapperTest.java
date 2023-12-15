package greencity.mapping;

import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getFactTranslation;
import static greencity.ModelUtils.getLanguageTranslationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Slf4j
class LanguageTranslationDtoMapperTest {

    private final LanguageTranslationDtoMapper mapper = new LanguageTranslationDtoMapper();

    @Test
    void convert() {

        HabitFactTranslation habitFactTranslation = getFactTranslation();
        log.info("habit: {}",habitFactTranslation);

        LanguageTranslationDTO expected = getLanguageTranslationDTO();
        log.info("expected: {}" , expected);

        LanguageTranslationDTO actual = mapper.convert(habitFactTranslation);
        log.info("actual: {}" , actual);

        assertEquals(expected.getLanguage().getCode(), actual.getLanguage().getCode());
        assertEquals(expected.getLanguage().getId(), actual.getLanguage().getId());
    }
}