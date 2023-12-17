package greencity.service.ticket292;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import greencity.service.HabitStatusCalendarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitStatusCalendarServiceImplTest {

    private HabitStatusCalendarRepo habitStatusCalendarRepo;
    private ModelMapper modelMapper;
    private HabitStatusCalendarServiceImpl habitStatusCalendarService;

    @BeforeEach
    void setUp() {
        habitStatusCalendarRepo = mock(HabitStatusCalendarRepo.class);
        modelMapper = new ModelMapper();
        habitStatusCalendarService = new HabitStatusCalendarServiceImpl(habitStatusCalendarRepo, modelMapper);
    }

    @Test
    void testFindHabitStatusCalendarByEnrollDateAndHabitAssign() {
        LocalDate date = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(eq(date), any()))
                .thenReturn(new HabitStatusCalendar());

        HabitStatusCalendarVO result = habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssignVO);

        assertNotNull(result);
    }

    @Test
    void testFindHabitStatusCalendarByEnrollDateAndHabitAssign_NotFound() {
        LocalDate date = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(eq(date), any()))
                .thenReturn(null);

        HabitStatusCalendarVO result = habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssignVO);

        assertNull(result);
    }

    @Test
    void testSave() {
        HabitStatusCalendarVO habitStatusCalendarVO = new HabitStatusCalendarVO();
        when(habitStatusCalendarRepo.save(any())).thenReturn(new HabitStatusCalendar());

        HabitStatusCalendarVO result = habitStatusCalendarService.save(habitStatusCalendarVO);

        assertNotNull(result);
    }

    @Test
    void testDelete() {
        HabitStatusCalendarVO habitStatusCalendarVO = new HabitStatusCalendarVO();

        assertDoesNotThrow(() -> habitStatusCalendarService.delete(habitStatusCalendarVO));
    }

    @Test
    void testFindTopByEnrollDateAndHabitAssign() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        when(habitStatusCalendarRepo.findTopByEnrollDateAndHabitAssign(any())).thenReturn(LocalDate.now());

        LocalDate result = habitStatusCalendarService.findTopByEnrollDateAndHabitAssign(habitAssignVO);

        assertNotNull(result);
    }

    @Test
    void testFindEnrolledDatesAfter() {
        LocalDate dateTime = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        when(habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitAssign(eq(dateTime), any()))
                .thenReturn(Collections.singletonList(new HabitStatusCalendar()));

        List<LocalDate> result = habitStatusCalendarService.findEnrolledDatesAfter(dateTime, habitAssignVO);

        assertFalse(result.isEmpty());
    }

    @Test
    void testFindEnrolledDatesBefore() {
        LocalDate dateTime = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        when(habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitAssign(eq(dateTime), any()))
                .thenReturn(Collections.singletonList(new HabitStatusCalendar()));

        List<LocalDate> result = habitStatusCalendarService.findEnrolledDatesBefore(dateTime, habitAssignVO);

        assertFalse(result.isEmpty());
    }

    @Test
    void testDeleteAllByHabitAssign() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();

        assertDoesNotThrow(() -> habitStatusCalendarService.deleteAllByHabitAssign(habitAssignVO));
    }
}