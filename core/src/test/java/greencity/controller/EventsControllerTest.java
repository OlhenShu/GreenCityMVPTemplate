package greencity.controller;

import greencity.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class EventsControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventsController eventsController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testDeleteEvent() {
        Long eventId = 1L;
        Principal principal = mock(Principal.class);

        ResponseEntity<Object> response = eventsController.delete(eventId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService, times(1)).delete(eventId, principal.getName());
    }
}
