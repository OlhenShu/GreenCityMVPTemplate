package greencity.dto.habit;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HabitManagementDto implements Serializable {
    private Long id;
    private String image;
    @Min(value = 1, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @Max(value = 3, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @NotNull
    private Integer complexity;
    @Valid
    private List<HabitTranslationManagementDto> habitTranslations;
    @NotNull
    private Integer defaultDuration;
}
