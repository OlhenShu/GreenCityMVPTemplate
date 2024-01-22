package greencity.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinates {
    private double latitude;

    private double longitude;
}
