package greencity.service;

import greencity.dto.event.EventDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

    EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images);

    void delete(Long eventId, String email);

}
