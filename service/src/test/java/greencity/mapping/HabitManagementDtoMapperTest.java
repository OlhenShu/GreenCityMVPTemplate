package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitManagementDtoMapperTest {
    @InjectMocks
    private HabitManagementDtoMapper habitManagementDtoMapper;

    @Test
    void convertTest() {
        Habit habit = Habit.builder()
            .id(1L)
            .image("image")
            .complexity(1)
            .defaultDuration(1)
            .tags(new HashSet<>(ModelUtils.getTags()))
            .shoppingListItems(Set.of(ShoppingListItem.builder()
                .id(1L)
                .translations(List.of(ShoppingListItemTranslation.builder()
                    .id(1L)
                    .content("content")
                    .language(ModelUtils.getLanguage())
                    .build()))
                .build()))
            .habitTranslations(List.of(HabitTranslation.builder()
                .id(1L)
                .description("description")
                .habitItem("habitItem")
                .name("name")
                .language(ModelUtils.getLanguage())
                .build()))
            .build();

        HabitManagementDto expected = HabitManagementDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .complexity(habit.getComplexity())
            .habitTranslations(habit.getHabitTranslations().stream()
                .map(habitTranslation -> HabitTranslationManagementDto.builder()
                    .id(habitTranslation.getId())
                    .description(habitTranslation.getDescription())
                    .habitItem(habitTranslation.getHabitItem())
                    .name(habitTranslation.getName())
                    .languageCode(habitTranslation.getLanguage().getCode())
                    .build())
                .collect(Collectors.toList()))
            .defaultDuration(habit.getDefaultDuration())
            .build();

        assertEquals(expected, habitManagementDtoMapper.convert(habit));
    }


}
