package greencity.controller;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventsController eventsController;
    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    void testCreateEvent() {
        AddEventDtoRequest addEventDtoRequest = new AddEventDtoRequest();
        MultipartFile[] images = new MultipartFile[0];

        UserVO mockUserVO = new UserVO();

        EventDto mockEventDto = new EventDto();
        when(eventService.save(addEventDtoRequest, mockUserVO, images)).thenReturn(mockEventDto);

        ResponseEntity<EventDto> response = eventsController.save(addEventDtoRequest, mockUserVO, images);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockEventDto, response.getBody());

        verify(eventService, times(1)).save(addEventDtoRequest, mockUserVO, images);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testGetEvent() {
        Long eventId = 1L;
        EventDto mockEventDto = new EventDto();

        when(eventService.getById(eventId)).thenReturn(mockEventDto);

        ResponseEntity<EventDto> response = eventsController.getEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockEventDto, response.getBody());

        verify(eventService, times(1)).getById(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void testUpdate() {
        UpdateEventDto updateEventDto = new UpdateEventDto();

        Principal principal = mock(Principal.class);

        MultipartFile[] images = new MultipartFile[0];

        when(principal.getName()).thenReturn(userVO.getEmail());
        when(eventService.update(updateEventDto, userVO.getEmail(), images)).thenReturn(new EventDto());

        ResponseEntity<EventDto> response = eventsController.update(updateEventDto, principal, images);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(eventService, times(1)).update(updateEventDto, principal.getName(), images);
        verifyNoMoreInteractions(eventService);
    }
}
