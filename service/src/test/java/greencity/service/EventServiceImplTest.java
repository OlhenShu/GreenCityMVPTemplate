package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.EventVO;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testDeleteEventByOrganizer() {
        Long eventId = 1L;
        String organizerEmail = "organizer@example.com";

        UserVO organizerUser = new UserVO();
        organizerUser.setId(1L);
        organizerUser.setEmail(organizerEmail);
        organizerUser.setRole(Role.ROLE_USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOrganizer(new User());

        when(restClient.findByEmail(organizerEmail)).thenReturn(organizerUser);
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() -> eventService.delete(eventId, organizerEmail));

        verify(eventRepo, times(1)).delete(event);
    }

    @Test
    void testDeleteEventByAdmin() {
        Long eventId = 1L;
        String adminEmail = "admin@example.com";

        UserVO adminUser = new UserVO();
        adminUser.setId(1L);
        adminUser.setEmail(adminEmail);
        adminUser.setRole(Role.ROLE_ADMIN);

        Event event = new Event();
        event.setId(eventId);
        event.setOrganizer(new User());

        when(restClient.findByEmail(adminEmail)).thenReturn(adminUser);
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() -> eventService.delete(eventId, adminEmail));

        verify(eventRepo, times(1)).delete(event);
    }

    @Test
    void testDeleteEventByNonAuthorizedUser() {
        Long eventId = 1L;
        String userEmail = "user@example.com";

        UserVO nonAuthorizedUser = new UserVO();
        nonAuthorizedUser.setId(2L);
        nonAuthorizedUser.setEmail(userEmail);
        nonAuthorizedUser.setRole(Role.ROLE_USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOrganizer(new User());

        when(restClient.findByEmail(userEmail)).thenReturn(nonAuthorizedUser);
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(UserHasNoPermissionToAccessException.class,
                () -> eventService.delete(eventId, userEmail));
    }

    @Test
    void testDeleteNonExistingEvent() {
        Long eventId = 1L;
        String userEmail = "user@example.com";

        when(restClient.findByEmail(userEmail)).thenReturn(new UserVO());
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.delete(eventId, userEmail));
    }

    @Test
    void testFindEventById() {
        Long eventId = 1L;

        Event event = new Event();
        event.setId(eventId);

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        EventVO result = eventService.findById(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
    }

    @Test
    void testFindNonExistingEventById() {
        Long eventId = 1L;

        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.findById(eventId));
    }
}
