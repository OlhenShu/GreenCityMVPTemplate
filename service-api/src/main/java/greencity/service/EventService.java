package greencity.service;

import greencity.dto.event.EventVO;

public interface EventService {
    void delete(Long eventId, String email);
    EventVO findById(Long eventId);

}