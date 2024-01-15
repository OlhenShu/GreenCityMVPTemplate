package greencity.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class Address {
    @Column
    private double latitude;
    @Column
    private double longitude;
    @Column
    private String streetEn;
    @Column
    private String streetUa;
    @Column
    private String houseNumber;
    @Column
    private String cityEn;
    @Column
    private String cityUa;
    @Column
    private String regionEn;
    @Column
    private String regionUa;
    @Column
    private String countryEn;
    @Column
    private String countryUa;
    @Column
    private String formattedAddressEn;
    @Column
    private String formattedAddressUa;
}
