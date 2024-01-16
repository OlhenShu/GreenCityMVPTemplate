package greencity.service;

import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.repository.EventCommentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;

    @Mock
    private EventService eventService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Test
    void testSaveEventComment() {
        Long eventId = 1L;
        AddEventCommentDtoRequest request = new AddEventCommentDtoRequest();
        request.setText("Test comment");

        UserVO userVO = new UserVO();
        userVO.setId(1L);

        EventVO eventVO = new EventVO();
        eventVO.setId(eventId);

        EventComment eventComment = new EventComment();
        eventComment.setId(1L);

        AddEventCommentDtoResponse expectedResponse = new AddEventCommentDtoResponse();
        expectedResponse.setId(1L);

        when(eventService.findById(eq(eventId))).thenReturn(eventVO);
        when(modelMapper.map(eq(request), eq(EventComment.class))).thenReturn(eventComment);
        when(modelMapper.map(eq(userVO), eq(User.class))).thenReturn(new User());
        when(modelMapper.map(eq(eventVO), eq(Event.class))).thenReturn(new Event());
        when(eventCommentRepo.save(any(EventComment.class))).thenReturn(eventComment);
        when(modelMapper.map(eq(eventComment), eq(AddEventCommentDtoResponse.class))).thenReturn(expectedResponse);
        when(modelMapper.map(eq(userVO), eq(EventCommentAuthorDto.class))).thenReturn(new EventCommentAuthorDto());

        AddEventCommentDtoResponse response = eventCommentService.save(eventId, request, userVO);

        assertEquals(expectedResponse, response);
    }
}
