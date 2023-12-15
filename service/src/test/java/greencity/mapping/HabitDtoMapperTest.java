package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import java.util.ArrayList;
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
public class HabitDtoMapperTest {
    @InjectMocks
    private HabitDtoMapper habitDtoMapper;

    @Test
    void convertTest() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
            .id(1L)
            .name("name")
            .description("description")
            .habitItem("habitItem")
            .language(ModelUtils.getLanguage())
            .habit(Habit.builder()
                .id(1L)
                .image("image")
                .complexity(1)
                .defaultDuration(1)
                .tags(new HashSet<>(ModelUtils.getTags()))
                .shoppingListItems(Set.of(ShoppingListItem.builder()
                    .id(1L)
                    .translations(List.of(ShoppingListItemTranslation.builder()
                        .content("content")
                        .language(ModelUtils.getLanguage())
                        .build()))
                    .build()))
                .build())
            .build();

        Habit habit = habitTranslation.getHabit();
        Language language = habitTranslation.getLanguage();

        HabitDto expected = HabitDto.builder()
            .defaultDuration(habit.getDefaultDuration())
            .habitTranslation(HabitTranslationDto.builder()
                .description(habitTranslation.getDescription())
                .habitItem(habitTranslation.getHabitItem())
                .name(habitTranslation.getName())
                .languageCode(language.getCode())
                .build())
            .id(habit.getId())
            .image(habit.getImage())
            .complexity(habit.getComplexity())
            .tags(habit.getTags().stream()
                .flatMap(tag -> tag.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().equals(language))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .shoppingListItems(habit.getShoppingListItems() != null ? habit.getShoppingListItems().stream()
                .map(shoppingListItem -> ShoppingListItemDto.builder()
                    .id(shoppingListItem.getId())
                    .status(ShoppingListItemStatus.ACTIVE.toString())
                    .text(shoppingListItem.getTranslations().stream()
                        .filter(shoppingListItemTranslation -> shoppingListItemTranslation
                            .getLanguage().equals(language))
                        .map(ShoppingListItemTranslation::getContent)
                        .findFirst().orElse(null))
                    .build())
                .collect(Collectors.toList()) : new ArrayList<>())
            .build();

        assertEquals(expected, habitDtoMapper.convert(habitTranslation));
    }
}
