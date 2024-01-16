package greencity.controller;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventCommentControllerTest {
    @Mock
    private EventCommentService eventCommentService;

    @InjectMocks
    private EventCommentController eventCommentController;

    @Test
    void testSaveEventComment() {
        Long eventId = 1L;
        AddEventCommentDtoRequest request = new AddEventCommentDtoRequest();
        request.setText("Test comment");

        UserVO user = new UserVO();
        user.setId(1L);

        AddEventCommentDtoResponse expectedResponse = new AddEventCommentDtoResponse();
        expectedResponse.setId(1L);

        when(eventCommentService.save(eq(eventId), eq(request), eq(user))).thenReturn(expectedResponse);

        ResponseEntity<AddEventCommentDtoResponse> responseEntity = eventCommentController.save(eventId, request, user);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }
}
