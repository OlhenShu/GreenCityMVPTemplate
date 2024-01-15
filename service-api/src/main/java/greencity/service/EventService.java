package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.search.SearchEventDto;
import org.springframework.data.domain.Pageable;
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
     * Method for getting all Events by searchQuery.
     *
     * @param pageable     {@link Pageable}.
     * @param searchQuery  query to search.
     * @param languageCode the language code to specify the desired language for event information.
     * @return PageableDto of {@link SearchEventDto} instances.
     * @author Nikita Malov & Denys Liubchenko
     */
    PageableDto<SearchEventDto> search(Pageable pageable, String searchQuery, String languageCode);
}
