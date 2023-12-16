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
public class HabitTranslationMapperTest {
    @InjectMocks
    private HabitTranslationMapper habitTranslationMapper;

    @Test
    void convertTest() {
        HabitTranslationDto habitTranslationDto = getHabitTranslationDto();

        HabitTranslation expected = getHabitTranslation(habitTranslationDto);

        assertEquals(expected, habitTranslationMapper.convert(habitTranslationDto));
    }

    @Test
    void mapAllToListTest() {
        HabitTranslationDto habitTranslationDto = getHabitTranslationDto();

        HabitTranslation expected = getHabitTranslation(habitTranslationDto);

        assertArrayEquals(List.of(expected).toArray(),
            habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)).toArray());
    }

    private HabitTranslationDto getHabitTranslationDto() {
        return HabitTranslationDto.builder()
            .name("name")
            .description("description")
            .habitItem("habitItem")
            .languageCode(ModelUtils.getLanguage().getCode())
            .build();
    }

    private HabitTranslation getHabitTranslation(HabitTranslationDto habitTranslationDto) {
        return HabitTranslation.builder()
            .name(habitTranslationDto.getName())
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .build();
    }

}
