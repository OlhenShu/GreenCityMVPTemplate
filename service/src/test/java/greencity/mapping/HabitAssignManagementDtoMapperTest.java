package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignManagementDtoMapperTest {
    @InjectMocks
    private HabitAssignManagementDtoMapper habitAssignManagementDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignManagementDto habitAssignManagementDto = HabitAssignManagementDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .createDateTime(habitAssign.getCreateDate())
            .habitId(habitAssign.getHabit().getId())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .habitStreak(habitAssign.getHabitStreak())
            .lastEnrollment(habitAssign.getLastEnrollmentDate())
            .build();

        assertEquals(habitAssignManagementDto, habitAssignManagementDtoMapper.convert(habitAssign));
    }
}
