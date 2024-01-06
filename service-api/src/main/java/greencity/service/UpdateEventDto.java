package greencity.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.dto.event.EventDateLocationDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class UpdateEventDto {

    @NotNull
    private Long id;

    @Size(min = 1, max = 70)
    private String title;

    @Size(min = 20, max = 63206)
    private String description;

    @Max(7)
    private List<EventDateLocationDto> datesLocations;

    private String titleImage;

    private List<String> additionalImages;

    private List<String> imagesToDelete;

    @JsonProperty(value = "open")
    private Boolean isOpen;

}
