package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.CommentStatus;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final EventRepo eventRepo;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest addEventCommentDtoRequest,
                                           UserVO userVO) {
        EventVO eventVO = eventService.findById(eventId);
        EventComment eventComment = modelMapper.map(addEventCommentDtoRequest, EventComment.class);
        eventComment.setUser(modelMapper.map(userVO, User.class));
        eventComment.setEvent(modelMapper.map(eventVO, Event.class));
        if (addEventCommentDtoRequest.getParentCommentId() != null
                && addEventCommentDtoRequest.getParentCommentId() > 0) {
            Long parentCommentId = addEventCommentDtoRequest.getParentCommentId();
            EventComment parentEventComment = eventCommentRepo.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId));

            if (parentEventComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }

            if (!parentEventComment.getEvent().getId().equals(eventId)) {
                String message = ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId
                        + " in event with id:" + eventId;
                throw new NotFoundException(message);
            }
            eventComment.setParentComment(parentEventComment);
        }
        eventComment.setStatus(CommentStatus.ORIGINAL);
        AddEventCommentDtoResponse addEventCommentDtoResponse = modelMapper.map(
                eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);

        addEventCommentDtoResponse.setAuthor(modelMapper.map(userVO, EventCommentAuthorDto.class));
        return addEventCommentDtoResponse;
    }

    @Override
    public EventCommentDto getEventCommentById(Long id, UserVO userVO) {
        EventComment eventComment = eventCommentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + id));

        if (userVO != null) {
            eventComment.setCurrentUserLiked(eventComment.getUsersLiked().stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId())));
        }

        return modelMapper.map(eventComment, EventCommentDto.class);
    }


    @Override
    public PageableDto<EventCommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long eventId) {
        Optional<Event> event = eventRepo.findById(eventId);

        if (event.isEmpty()) {
            throw new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId);
        }

        Page<EventComment> pages =
                eventCommentRepo.findAllByParentCommentIdIsNullAndEventIdAndStatusNotOrderByCreatedDateDesc(pageable,
                        eventId, CommentStatus.DELETED);

        if (userVO != null) {
            pages.forEach(eventComment -> eventComment.setCurrentUserLiked(eventComment.getUsersLiked()
                    .stream()
                    .anyMatch(u -> u.getId().equals(userVO.getId()))));
        }

        List<EventCommentDto> eventCommentDto = pages
                .stream()
                .map(eventComment -> modelMapper.map(eventComment, EventCommentDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                eventCommentDto,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    @Override
    @Transactional
    public void update(String commentText, Long id, UserVO userVO) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(id, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (!userVO.getId().equals(eventComment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }

        eventComment.setText(commentText);
        eventComment.setStatus(CommentStatus.EDITED);
        eventCommentRepo.save(eventComment);
    }

    @Transactional
    @Override
    public void delete(Long eventCommentId, UserVO user) {
        EventComment eventComment = eventCommentRepo
                .findByIdAndStatusNot(eventCommentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + eventCommentId));

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventComment.setStatus(CommentStatus.DELETED);
        if (eventComment.getComments() != null) {
            eventComment.getComments()
                    .forEach(comment -> comment.setStatus(CommentStatus.DELETED));
        }
        eventCommentRepo.save(eventComment);
    }

}