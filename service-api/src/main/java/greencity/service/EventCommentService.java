package greencity.service;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

public interface EventCommentService {
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                    UserVO user);
}