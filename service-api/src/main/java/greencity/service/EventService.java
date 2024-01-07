package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Updates an existing event with the provided data.
     *
     * @param eventDto The data to update the event.
     * @param email    The email of the user performing the update.
     * @param images   An array of image files associated with the event.
     * @return The updated event DTO.
     */
    EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images);
}
