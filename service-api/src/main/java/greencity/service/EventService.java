package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id The unique identifier of the event.
     * @return The event DTO if found, or null if no event with the given identifier exists.
     */
    EventDto getById(Long id);

    /**
     * Updates an existing event with the provided data.
     *
     * @param eventDto The data to update the event.
     * @param email    The email of the user performing the update.
     * @param images   An array of image files associated with the event.
     * @return The updated event DTO.
     */
    EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images);

    /**
     * Retrieves the total number of events associated with the specified user.
     *
     * @param userId The unique identifier of the user for whom the event count is to be obtained.
     * @return The total number of events associated with the specified user.
     */
    Long getAmountOfEvents(Long userId);
}
