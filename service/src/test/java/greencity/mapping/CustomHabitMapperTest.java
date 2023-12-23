package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CustomHabitMapperTest {

    @InjectMocks
    private CustomHabitMapper customHabitMapper;

    @Test
    public void convertTest() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = AddCustomHabitDtoRequest.builder()
            .image("test.png")
            .complexity(1)
            .defaultDuration(1)
            .build();

        Habit expected = Habit.builder()
            .image(addCustomHabitDtoRequest.getImage())
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();

        assertEquals(expected, customHabitMapper.convert(addCustomHabitDtoRequest));
    }
}
