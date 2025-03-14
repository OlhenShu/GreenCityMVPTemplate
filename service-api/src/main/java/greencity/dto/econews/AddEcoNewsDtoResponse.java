package greencity.dto.econews;

import greencity.dto.user.EcoNewsAuthorDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddEcoNewsDtoResponse implements Serializable {
    @Min(1)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String text;

    private String shortInfo;
    @NotEmpty
    private EcoNewsAuthorDto ecoNewsAuthorDto;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    @NotEmpty
    private String imagePath;

    private String source;

    @NotEmpty
    private List<String> tags;
}
