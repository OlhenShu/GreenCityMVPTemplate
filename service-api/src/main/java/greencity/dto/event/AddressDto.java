package greencity.dto.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
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
