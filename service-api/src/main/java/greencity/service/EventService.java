package greencity.service;

import greencity.dto.event.RequestAddEventDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    /**
     * Method for creating {@link EventDto} instance.
     *
     * @param requestAddEventDto - dto with {@link RequestAddEventDto} entered info about field that need.
     * @param userVO                {@link UserVO} - current user.
     * @param images                {@link List<MultipartFile>} - optional to fill png files.
     *
     * @return {@link EventDto} instance.
     */
    EventDto save (RequestAddEventDto requestAddEventDto, UserVO userVO, List<MultipartFile> images);
}
