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
     * Method to upload news image.
     *
     * @param image - eco news image
     * @return image path
     */
    String uploadImage(MultipartFile image);

    /**
     * Method to upload news images.
     *
     * @param images - array of eco news images
     * @return array of images path
     */
    String[] uploadImages(MultipartFile[] images);
}
