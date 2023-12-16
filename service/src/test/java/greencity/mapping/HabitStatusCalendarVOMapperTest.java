package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitStatusCalendarVOMapperTest {
    @InjectMocks
    private HabitStatusCalendarVOMapper habitStatusCalendarVOMapper;

    @Test
    void convertTest() {
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        habitStatusCalendar.setHabitAssign(ModelUtils.getHabitAssign());

        HabitStatusCalendarVO expected = HabitStatusCalendarVO.builder()
            .id(habitStatusCalendar.getId())
            .enrollDate(habitStatusCalendar.getEnrollDate())
            .habitAssignVO(HabitAssignVO.builder()
                .id(habitStatusCalendar.getHabitAssign().getId())
                .build())
            .build();

        assertEquals(expected, habitStatusCalendarVOMapper.convert(habitStatusCalendar));
    }
}
