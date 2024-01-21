package greencity.service;


import greencity.dto.event.EventVO;

public interface EventService {
    /**
     * Method for deleting Event instance.
     *
     * @param eventId - event id.
     * @param email   - user email.
     */
    void delete(Long eventId, String email);

    /**
     * Get event by id.
     *
     * @param eventId - id of event
     */
    EventVO findById(Long eventId);
}