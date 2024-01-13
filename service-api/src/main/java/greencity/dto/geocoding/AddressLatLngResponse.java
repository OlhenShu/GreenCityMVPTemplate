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
    private String streetEn;
    private String streetUa;
    private String houseNumber;
    private String cityEn;
    private String cityUa;
    private String regionEn;
    private String regionUa;
    private String countryEn;
    private String countryUa;
    private String formattedAddressEn;
    private String formattedAddressUa;
}
