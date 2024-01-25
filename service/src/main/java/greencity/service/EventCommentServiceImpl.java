package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.NotificationSourceType;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final HttpServletRequest httpServletRequest;
    private final RatingCalculation ratingCalculation;
    private final NotificationService notificationService;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                           UserVO userVO) {
        EventVO eventVO = eventService.findById(eventId);
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setUser(modelMapper.map(userVO, User.class));
        eventComment.setEvent(modelMapper.map(eventVO, Event.class));

        AddEventCommentDtoResponse addEventCommentDtoResponse = modelMapper.map(
                eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);

        addEventCommentDtoResponse.setAuthor(modelMapper.map(userVO, EventCommentAuthorDto.class));

        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));

        notificationService.createNotificationForEvent(userVO, eventVO, NotificationSourceType.EVENT_COMMENTED);
        return addEventCommentDtoResponse;
    }
}