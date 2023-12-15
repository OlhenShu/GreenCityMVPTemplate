package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignMapperTest {
    @InjectMocks
    private HabitAssignMapper habitAssignMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        ArrayList<UserShoppingListItem> userShoppingListItemArrayList = new ArrayList<>();
        UserShoppingListItem shoppingListItem = ModelUtils.getUserShoppingListItem();
        shoppingListItem.setStatus(ShoppingListItemStatus.INPROGRESS);
        userShoppingListItemArrayList.add(ModelUtils.getUserShoppingListItem());
        userShoppingListItemArrayList.add(shoppingListItem);
        habitAssign.setUserShoppingListItems(userShoppingListItemArrayList);

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
            .createDateTime(habitAssign.getCreateDate())
            .duration(habitAssign.getDuration())
            .habit(HabitDto.builder()
                .id(habitAssign.getHabit().getId())
                .complexity(habitAssign.getHabit().getComplexity())
                // TODO : assign defaultDuration from habitAssign.getDuration() to habitAssign.getHabit().getDefaultDuration()
                .defaultDuration(habitAssign.getDuration())
                .build())
            .userShoppingListItems(habitAssign.getUserShoppingListItems().stream().map(
                    userShoppingListItem -> UserShoppingListItemAdvanceDto.builder()
                        .id(userShoppingListItem.getId())
                        .dateCompleted(userShoppingListItem.getDateCompleted())
                        .status(userShoppingListItem.getStatus())
                        .shoppingListItemId(userShoppingListItem.getShoppingListItem().getId())
                        .build())
                .collect(Collectors.toList()))
            .habitStreak(habitAssign.getHabitStreak())
            .id(habitAssign.getId())
            .lastEnrollmentDate(habitAssign.getLastEnrollmentDate())
            .status(habitAssign.getStatus())
            .workingDays(habitAssign.getWorkingDays())
            .progressNotificationHasDisplayed(habitAssign.getProgressNotificationHasDisplayed())
            .build();

        HabitAssign expected = HabitAssign.builder()
            .id(habitAssignDto.getId())
            .createDate(habitAssignDto.getCreateDateTime())
            .status(habitAssignDto.getStatus())
            .duration(habitAssignDto.getDuration())
            .workingDays(habitAssignDto.getWorkingDays())
            .habitStreak(habitAssignDto.getHabitStreak())
            .lastEnrollmentDate(habitAssignDto.getLastEnrollmentDate())
            .progressNotificationHasDisplayed(habitAssignDto.getProgressNotificationHasDisplayed())
            .userShoppingListItems(habitAssignDto.getUserShoppingListItems().stream()
                .filter(userShoppingListItemAdvanceDto ->
                    userShoppingListItemAdvanceDto.getStatus() == ShoppingListItemStatus.INPROGRESS)
                .map(userShoppingListItemAdvanceDto -> greencity.entity.UserShoppingListItem.builder()
                    .id(userShoppingListItemAdvanceDto.getId())
                    .dateCompleted(userShoppingListItemAdvanceDto.getDateCompleted())
                    .status(userShoppingListItemAdvanceDto.getStatus())
                    .shoppingListItem(greencity.entity.ShoppingListItem.builder()
                        .id(userShoppingListItemAdvanceDto.getShoppingListItemId())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .habit(Habit.builder()
                .id(habitAssignDto.getHabit().getId())
                .complexity(habitAssignDto.getHabit().getComplexity())
                .defaultDuration(habitAssignDto.getHabit().getDefaultDuration())
                .build())
            .build();

        assertEquals(expected, habitAssignMapper.convert(habitAssignDto));
    }
}
