package greencity.dto.geocoding;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class AddressLatLngResponse {
    private double latitude;
    private double longitude;
    private AddressResponse addressEn;
    private AddressResponse addressUa;
}
