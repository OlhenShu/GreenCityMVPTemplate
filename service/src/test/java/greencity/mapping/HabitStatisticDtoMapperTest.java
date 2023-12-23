package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitStatisticDtoMapperTest {
    @InjectMocks
    private HabitStatisticDtoMapper habitStatisticDtoMapper;

    @Test
    void convertTest() {
        HabitStatistic habitStatistic = ModelUtils.getHabitStatistic();
        habitStatistic.setHabitAssign(ModelUtils.getHabitAssign());

        HabitStatisticDto expected = HabitStatisticDto.builder()
            .id(habitStatistic.getId())
            .habitRate(habitStatistic.getHabitRate())
            .createDate(habitStatistic.getCreateDate())
            .amountOfItems(habitStatistic.getAmountOfItems())
            .habitAssignId(habitStatistic.getHabitAssign().getId())
            .build();

        assertEquals(expected, habitStatisticDtoMapper.convert(habitStatistic));
    }
}
