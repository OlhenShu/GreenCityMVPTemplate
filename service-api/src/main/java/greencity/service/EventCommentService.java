package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {

    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                    UserVO user);
    EventCommentDto getEventCommentById(Long id, UserVO userVO);
    PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long eventId);
    void update(String commentText, Long id, UserVO user);
    void delete(Long eventCommentId, UserVO user);

}