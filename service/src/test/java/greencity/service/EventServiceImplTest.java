package greencity.service;

import static greencity.ModelUtils.getUserVO;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EventServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private EventRepo eventRepo;
    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void getAmountOfEvents(){
        UserVO userVO = getUserVO();
        Long expected = 5L;

        when(userRepo.existsById(userVO.getId())).thenReturn(true);
        when(eventRepo.countByOrganizerId(userVO.getId())).thenReturn(expected);
        Long actual = eventService.getAmountOfEvents(userVO.getId());

        verify(userRepo).existsById(userVO.getId());
        verify(eventRepo).countByOrganizerId(userVO.getId());
        assertEquals(expected,actual);
    }

    @Test
    void getAmountOfEventsByNotExistingUserIdThrowsException(){
        UserVO userVO = getUserVO();
        when(userRepo.existsById(userVO.getId())).thenReturn(false);

        assertThrows(NotFoundException.class,
            () -> eventService.getAmountOfEvents(userVO.getId()));
        verify(userRepo).existsById(userVO.getId());
    }
}
