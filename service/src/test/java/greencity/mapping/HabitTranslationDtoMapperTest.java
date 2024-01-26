package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitTranslationDtoMapperTest {
    @InjectMocks
    private HabitTranslationDtoMapper habitTranslationDtoMapper;

    @Test
    void convertTest() {
        HabitTranslation habitTranslation = getHabitTranslation();

        HabitTranslationDto expected = getHabitTranslationDto(habitTranslation);

        assertEquals(expected, habitTranslationDtoMapper.convert(habitTranslation));
    }

    @Test
    void mapAllToListTest() {
        HabitTranslation habitTranslation = getHabitTranslation();

        HabitTranslationDto expected = getHabitTranslationDto(habitTranslation);

        assertArrayEquals(List.of(expected).toArray(),
            habitTranslationDtoMapper.mapAllToList(List.of(habitTranslation)).toArray());
    }

    private HabitTranslationDto getHabitTranslationDto(HabitTranslation habitTranslation) {
        return HabitTranslationDto.builder()
            .name(habitTranslation.getName())
            .description(habitTranslation.getDescription())
            .habitItem(habitTranslation.getHabitItem())
            .languageCode(habitTranslation.getLanguage().getCode())
            .build();
    }

    private HabitTranslation getHabitTranslation() {
        return HabitTranslation.builder()
            .id(1L)
            .name("name")
            .description("description")
            .habitItem("habitItem")
            .language(ModelUtils.getLanguage())
            .build();
    }
}
