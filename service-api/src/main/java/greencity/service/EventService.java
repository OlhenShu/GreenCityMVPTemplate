package greencity.service;

import greencity.dto.event.AddEventDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    EventDto save (AddEventDto addEventDto, UserVO userVO, List<MultipartFile> images);

}
