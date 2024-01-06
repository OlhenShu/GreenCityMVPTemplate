package greencity.dto.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesDto {

    private double latitude;
    private double longitude;
}
