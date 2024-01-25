package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.search.SearchEventDto;
import org.springframework.data.domain.Pageable;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;
import greencity.dto.event.EventVO;

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

    /**
     * Method for deleting Event instance.
     *
     * @param eventId - event id.
     * @param email   - user email.
     */
    void delete(Long eventId, String email);
   

    /**
     * Retrieves the total number of events associated with the specified user.
     *
     * @param userId The unique identifier of the user for whom the event count is to be obtained.
     * @return The total number of events associated with the specified user.
     */
    Long getAmountOfEvents(Long userId);

    /**
     * Method for creating {@link EventDto} instance.
     *
     * @param addEventDtoRequest dto with {@link AddEventDtoRequest} entered info about field that need.
     * @param userVO             {@link UserVO} - current user.
     * @param images             optional to fill png files.
     * @return {@link EventDto} instance.
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, UserVO userVO, MultipartFile[] images);

    /**
     * Get event by id.
     *
     * @param eventId - id of event
     */
    EventVO findById(Long eventId);
}