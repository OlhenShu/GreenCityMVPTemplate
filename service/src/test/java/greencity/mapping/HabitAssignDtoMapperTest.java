package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignDtoMapperTest {
    @InjectMocks
    private HabitAssignDtoMapper habitAssignDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignDto expected = HabitAssignDto.builder()
            .createDateTime(habitAssign.getCreateDate())
            .duration(habitAssign.getDuration())
            .habitStatusCalendarDtoList(habitAssign.getHabitStatusCalendars().stream().map(
                    habitStatusCalendar -> HabitStatusCalendarDto.builder()
                        .id(habitStatusCalendar.getId())
                        .enrollDate(habitStatusCalendar.getEnrollDate())
                        .build())
                .collect(Collectors.toList()))
            .habitStreak(habitAssign.getHabitStreak())
            .id(habitAssign.getId())
            .lastEnrollmentDate(habitAssign.getLastEnrollmentDate())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .workingDays(habitAssign.getWorkingDays())
            .progressNotificationHasDisplayed(habitAssign.getProgressNotificationHasDisplayed())
            .build();

        assertEquals(expected, habitAssignDtoMapper.convert(habitAssign));
    }
}
