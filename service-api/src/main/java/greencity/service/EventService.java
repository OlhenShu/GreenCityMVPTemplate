package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * Method for creating {@link EventDto} instance.
     *
     * @param addEventDtoRequest dto with {@link AddEventDtoRequest} entered info about field that need.
     * @param userVO             {@link UserVO} - current user.
     * @param images             optional to fill png files.
     * @return {@link EventDto} instance.
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, UserVO userVO, MultipartFile[] images);
}
