package greencity.service.ticket292;

import greencity.converters.DateService;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatistic.*;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import greencity.enums.HabitRate;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;;
import greencity.service.HabitStatisticServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitStatisticServiceImplTest {

    @Mock
    private HabitStatisticRepo habitStatisticRepo;

    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private DateService dateService;

    @InjectMocks
    private HabitStatisticServiceImpl habitStatisticService;

    @Test
    void saveByHabitIdAndUserId_WhenHabitStatisticAlreadyExists_ShouldThrowNotSavedException() {
        // Arrange
        when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(any(), anyLong(), anyLong()))
                .thenReturn(Optional.of(new HabitStatistic()));

        // Act & Assert
        assertThrows(NotSavedException.class, () ->
                habitStatisticService.saveByHabitIdAndUserId(1L, 2L, new AddHabitStatisticDto()));
    }

    @Test
    void testSaveByHabitIdAndUserId_StatisticAlreadyExists() {
        // Arrange
        AddHabitStatisticDto dto = new AddHabitStatisticDto();
        Long habitId = 1L;
        Long userId = 1L;
        when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(any(), any(), any())).thenReturn(Optional.of(new HabitStatistic()));
        // Act & Assert
        assertThrows(NotSavedException.class, () -> habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto));
    }

    @Test
    void saveByHabitIdAndUserId_WhenDateIsNotTodayOrYesterday_ShouldThrowBadRequestException() {
        // Arrange
        when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(any(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        // Mocking dateService to return a ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(2); // Setting date older than yesterday
        when(dateService.convertToDatasourceTimezone(any()))
                .thenReturn(zonedDateTime);

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                habitStatisticService.saveByHabitIdAndUserId(1L, 2L, new AddHabitStatisticDto()));
    }

    @Test
    void update_WhenHabitStatisticNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(habitStatisticRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                habitStatisticService.update(1L, 2L, new UpdateHabitStatisticDto()));
    }

    @Test
    void update_WhenHabitStatisticNotBelongsToUser_ShouldThrowBadRequestException() {
        // Arrange
        UpdateHabitStatisticDto dto = new UpdateHabitStatisticDto();

        Habit habit = new Habit();
        habit.setId(1L);

        User user = new User();
        user.setId(2L);

        HabitAssign habitAssign = HabitAssign.builder()
                .habit(habit)
                .user(user)
                .createDate(ZonedDateTime.now())
                .status(HabitAssignStatus.ACTIVE)
                .duration(1)
                .workingDays(5)
                .habitStreak(0)
                .lastEnrollmentDate(ZonedDateTime.now())
                .progressNotificationHasDisplayed(false)
                .userShoppingListItems(List.of())
                .habitStatistic(List.of())
                .habitStatusCalendars(List.of())
                .build();

        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setHabitAssign(habitAssign);

        when(habitStatisticRepo.findById(anyLong())).thenReturn(Optional.of(habitStatistic));

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                habitStatisticService.update(1L, 3L, dto));
    }

    @Test
    void findById_WhenHabitStatisticNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(habitStatisticRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                habitStatisticService.findById(1L));
    }

    @Test
    void findAllStatsByHabitAssignId_WhenHabitAssignNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(habitAssignRepo.findById(anyLong())).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                habitStatisticService.findAllStatsByHabitAssignId(1L));
    }

    @Test
    void findAllStatsByHabitId_WhenHabitNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                habitStatisticService.findAllStatsByHabitId(1L));
    }

    @Test
    void findAllStatsByHabitId_WhenHabitFound_ShouldReturnGetHabitStatisticDto() {
        Habit habit = new Habit();
        habit.setId(1L);
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitStatisticRepo.findAllByHabitId(anyLong())).thenReturn(Collections.emptyList());
        // Act
        GetHabitStatisticDto result = habitStatisticService.findAllStatsByHabitId(1L);
        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getAmountOfUsersAcquired());
        assertEquals(0, result.getHabitStatisticDtoList().size());
    }

    @Test
    public void testFindAllStatsByHabitAssignId_NonExistingHabitAssign_ThrowsNotFoundException() {
        // Given
        Long habitAssignId = 1L;
        given(habitAssignRepo.findById(habitAssignId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> habitStatisticService.findAllStatsByHabitAssignId(habitAssignId));
        verify(habitAssignRepo).findById(habitAssignId);
        verifyNoInteractions(habitStatisticRepo);
    }

    @Test
    void getTodayStatisticsForAllHabitItems_WhenNoStatisticsAvailable_ShouldReturnEmptyList() {
        // Arrange
        when(habitStatisticRepo.getStatisticsForAllHabitItemsByDate(any(), anyString()))
                .thenReturn(List.of());
        // Act
        List<HabitItemsAmountStatisticDto> result = habitStatisticService.getTodayStatisticsForAllHabitItems("en");
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getTodayStatisticsForAllHabitItems_WhenStatisticsNotFound_ShouldReturnEmptyList() {
        // Arrange
        when(habitStatisticRepo.getStatisticsForAllHabitItemsByDate(any(), any())).thenReturn(Collections.emptyList());
        // Act
        List<HabitItemsAmountStatisticDto> result = habitStatisticService.getTodayStatisticsForAllHabitItems("en");
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAmountOfHabitsInProgressByUserId_WhenValidUserId_ShouldReturnAmount() {
        // Arrange
        Long userId = 1L;
        when(habitStatisticRepo.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(5L);
        // Act
        Long result = habitStatisticService.getAmountOfHabitsInProgressByUserId(userId);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void getAmountOfAcquiredHabitsByUserId_WhenValidUserId_ShouldReturnAmount() {
        Long userId = 1L;
        when(habitStatisticRepo.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(10L);

        Long result = habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId);

        assertEquals(10L, result);
    }

    @Test
    void testEnhanceHabitStatWithDto_ShouldUpdateHabitStatistic() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        UpdateHabitStatisticDto dto = new UpdateHabitStatisticDto();
        dto.setAmountOfItems(5);
        dto.setHabitRate(HabitRate.NORMAL);

        HabitStatistic updatable = new HabitStatistic();

        Method method = HabitStatisticServiceImpl.class.getDeclaredMethod("enhanceHabitStatWithDto", UpdateHabitStatisticDto.class, HabitStatistic.class);
        method.setAccessible(true);

        method.invoke(habitStatisticService, dto, updatable);

        assertEquals(dto.getAmountOfItems(), updatable.getAmountOfItems());
        assertEquals(dto.getHabitRate(), updatable.getHabitRate());
    }

    @Test
    void deleteAllStatsByHabitAssign_WhenValidHabitAssignVO_ShouldDeleteStats() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        habitAssignVO.setId(1L);
        when(habitStatisticRepo.findAllByHabitAssignId(habitAssignVO.getId()))
                .thenReturn(List.of(new HabitStatistic()));

        habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);

        verify(habitStatisticRepo, times(1)).delete(any());
    }

    @Test
    void testGetTodayStatisticsForAllHabitItems_EmptyResults() {
        when(habitStatisticRepo.getStatisticsForAllHabitItemsByDate(any(), any())).thenReturn(Collections.emptyList());

        List<HabitItemsAmountStatisticDto> actualStatistics = habitStatisticService.getTodayStatisticsForAllHabitItems("en");
        assertTrue(actualStatistics.isEmpty());
    }
}

