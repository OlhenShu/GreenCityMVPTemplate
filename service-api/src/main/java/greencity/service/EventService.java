package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    /**
     * Method for creating {@link EventDto} instance.
     *
     * @param addEventDtoRequest dto with {@link AddEventDtoRequest} entered info about field that need.
     * @param userVO             {@link UserVO} - current user.
     * @param images             optional to fill png files.
     * @return {@link EventDto} instance.
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, UserVO userVO, List<MultipartFile> images);
}
