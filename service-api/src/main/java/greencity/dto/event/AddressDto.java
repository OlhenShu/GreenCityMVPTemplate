package greencity.dto.event;

import greencity.dto.geocoding.AddressResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    double latitude;
    double longitude;
    AddressResponse addressEn;
    AddressResponse addressUa;
//    String cityEn;
//    String cityUa;
//    String countryEn;
//    String countryUa;
//    String formattedAddressEn;
//    String formattedAddressUa;
//    String houseNumber;
//    String regionEn;
//    String regionUa;
//    String streetEn;
//    String streetUa;
}
