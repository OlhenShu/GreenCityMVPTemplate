package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignUserDurationDtoMapperTest {
    @InjectMocks
    private HabitAssignUserDurationDtoMapper habitAssignUserDurationDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignUserDurationDto expected = HabitAssignUserDurationDto.builder()
            .habitAssignId(habitAssign.getId())
            .userId(habitAssign.getUser().getId())
            .habitId(habitAssign.getHabit().getId())
            .status(habitAssign.getStatus())
            .workingDays(habitAssign.getWorkingDays())
            .duration(habitAssign.getDuration())
            .build();

        assertEquals(expected, habitAssignUserDurationDtoMapper.convert(habitAssign));

    }
}
