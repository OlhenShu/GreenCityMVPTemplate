package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import greencity.constant.ErrorMessage;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.geocoding.AddressResponse;
import greencity.exception.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleApiService {
    private final GeoApiContext context;
    private static final Locale UKRAINIAN = new Locale("uk");
    private static final Locale ENGLISH = new Locale("en");

    /**
     * Send request to the Google and receive response with geocoding.
     *
     * @param searchCoordinates {@link LatLng} - coordinates to search
     * @return {@link AddressLatLngResponse} - return result from geocoding service
     */
    public AddressLatLngResponse getResultFromGeoCodeByCoordinates(LatLng searchCoordinates) {
        AddressLatLngResponse addressLatLngResponse = AddressLatLngResponse
                .builder()
                .latitude(searchCoordinates.lat)
                .longitude(searchCoordinates.lng)
                .build();

        AddressResponse addressUa = getAddressResponseByLocaleAndCoordinates(searchCoordinates, UKRAINIAN);
        if (addressUa == null) {
            throw new BadRequestException("Address with ukrainian is required!");
        }
        AddressResponse addressEn = getAddressResponseByLocaleAndCoordinates(searchCoordinates, ENGLISH);
        if (addressEn == null) {
            throw new BadRequestException("Address with english is required!");
        }
        addressLatLngResponse.setStreetUa(addressUa.getStreet());
        addressLatLngResponse.setHouseNumber(addressUa.getHouseNumber());
        addressLatLngResponse.setCityUa(addressUa.getCity());
        addressLatLngResponse.setRegionUa(addressUa.getRegion());
        addressLatLngResponse.setCountryUa(addressUa.getCountry());
        addressLatLngResponse.setFormattedAddressUa(addressUa.getFormattedAddress());

        addressLatLngResponse.setStreetEn(addressEn.getStreet());
        addressLatLngResponse.setHouseNumber(addressEn.getHouseNumber());
        addressLatLngResponse.setCityEn(addressEn.getCity());
        addressLatLngResponse.setRegionEn(addressEn.getRegion());
        addressLatLngResponse.setCountryEn(addressEn.getCountry());
        addressLatLngResponse.setFormattedAddressEn(addressEn.getFormattedAddress());

        return addressLatLngResponse;
    }

    private AddressResponse getAddressResponseByLocaleAndCoordinates(LatLng latLng, Locale locale) {
        try {
            GeocodingResult[] results = GeocodingApi.newRequest(context)
                    .latlng(latLng).language(locale.getLanguage()).await();
            if (results != null && results.length > 0) {
                return getAddressResponse(results[0]);
            } else {
                throw new BadRequestException(ErrorMessage.ADDRESS_NOT_FOUND_EXCEPTION);
            }
        } catch (IOException | InterruptedException | ApiException e) {
            log.error("Occurred error during the call on google API, reason: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private AddressResponse getAddressResponse(GeocodingResult geocodingResult) {
        AddressComponent[] addressComponents = geocodingResult.addressComponents;
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setFormattedAddress(geocodingResult.formattedAddress);
        for (AddressComponent component : addressComponents) {
            List<AddressComponentType> componentTypes = Arrays.asList(component.types);
            if (componentTypes.contains(AddressComponentType.STREET_NUMBER)) {
                addressResponse.setHouseNumber(component.longName);
            } else if (componentTypes.contains(AddressComponentType.ROUTE)) {
                addressResponse.setStreet(component.longName);
            } else if (componentTypes.contains(AddressComponentType.LOCALITY)) {
                addressResponse.setCity(component.longName);
            } else if (componentTypes.contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                addressResponse.setRegion(component.longName);
            } else if (componentTypes.contains(AddressComponentType.COUNTRY)) {
                addressResponse.setCountry(component.longName);
            }
        }
        return addressResponse;
    }
}
