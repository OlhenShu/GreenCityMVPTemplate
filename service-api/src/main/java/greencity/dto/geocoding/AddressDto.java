package greencity.dto.geocoding;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    String cityEn;
    String cityUa;
    String countryEn;
    String countryUa;
    String formattedAddressEn;
    String formattedAddressUa;
    String houseNumber;
    double latitude;
    double longitude;
    String regionEn;
    String regionUa;
    String streetEn;
    String streetUa;
}
