package greencity.dto.tag;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagUaEnDto {
    private long id;
    private String nameUa;
    private String nameEn;
}
