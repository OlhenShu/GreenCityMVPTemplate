package greencity.dto.event;

import lombok.*;

import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    double latitude;
    double longitude;
    String streetEn;
    String streetUa;
    String houseNumber;
    String cityEn;
    String cityUa;
    String regionEn;
    String regionUa;
    String countryEn;
    String countryUa;
    String formattedAddressEn;
    String formattedAddressUa;
}
