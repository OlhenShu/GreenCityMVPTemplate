package greencity.dto.language;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(of = {"id", "code"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageDTO {
    @Min(1)
    private Long id;

    @NotNull
    private String code;
}
