package greencity.dto.geocoding;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class AddressResponse {
    private String street;
    private String houseNumber;
    private String city;
    private String region;
    private String country;
    private String formattedAddress;
}
