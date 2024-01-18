package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.dto.habitstatistic.*;
import greencity.service.HabitStatisticService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {
    private MockMvc mockMvc;
    @Mock
    HabitStatisticService habitStatisticService;
    @InjectMocks
    HabitStatisticController habitStatisticController;
    ObjectMapper objectMapper = new ObjectMapper();
    private static final long existId = 1;
    private static final String habitStatisticResource = "/habit/statistic";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .build();
    }

    @Test
    void findAllByHabitId() throws Exception {
        GetHabitStatisticDto habitStatisticDto = new GetHabitStatisticDto();
        habitStatisticDto.setHabitStatisticDtoList(Collections.singletonList(new HabitStatisticDto()));

        when(habitStatisticService.findAllStatsByHabitId(existId)).thenReturn(habitStatisticDto);
        mockMvc.perform(get(habitStatisticResource + "/{habitId}", existId))
                .andExpect(status().isOk());

        ResponseEntity<GetHabitStatisticDto> response = habitStatisticController.findAllByHabitId(existId);
        Assertions.assertEquals(habitStatisticDto, response.getBody());
    }

    @Test
    void findAllStatsByHabitAssignId() throws Exception {
        List<HabitStatisticDto> habitStatisticDtoList = Collections.singletonList(new HabitStatisticDto());

        when(habitStatisticService.findAllStatsByHabitAssignId(existId)).thenReturn(habitStatisticDtoList);
        mockMvc.perform(get(habitStatisticResource + "/assign/{habitAssignId}", existId))
                .andExpect(status().isOk());

        ResponseEntity<List<HabitStatisticDto>> response =
                habitStatisticController.findAllStatsByHabitAssignId(existId);
        Assertions.assertEquals(habitStatisticDtoList, response.getBody());
    }

    @Test
    void saveHabitStatistic() throws Exception {
        AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto(
                existId
                , addHabitStatisticDto.getHabitRate()
                , addHabitStatisticDto.getCreateDate()
                , addHabitStatisticDto.getAmountOfItems()
                , existId);

        lenient().when(habitStatisticService.saveByHabitIdAndUserId(eq(existId), anyLong(), eq(addHabitStatisticDto)))
                .thenReturn(habitStatisticDto);

        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(addHabitStatisticDto);

        mockMvc.perform(post(habitStatisticResource +"/{habitId}", existId)
                        .principal(ModelUtils.getPrincipal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());
        addHabitStatisticDto =
                objectMapper.readValue(content, AddHabitStatisticDto.class);
        verify(habitStatisticService).saveByHabitIdAndUserId(eq(existId), nullable(Long.class), eq(addHabitStatisticDto));
    }

    @Test
    void updateStatistic() throws Exception {
        UpdateHabitStatisticDto updateHabitStatisticDto = new UpdateHabitStatisticDto(
                ModelUtils.addHabitStatisticDto().getAmountOfItems(),
                ModelUtils.addHabitStatisticDto().getHabitRate());

        lenient().when(habitStatisticService.update(eq(existId), anyLong(), eq(updateHabitStatisticDto)))
                .thenReturn(updateHabitStatisticDto);

        String content = objectMapper.writeValueAsString(updateHabitStatisticDto);

        mockMvc.perform(put(habitStatisticResource + "/{habitId}", existId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
        verify(habitStatisticService).update(eq(existId), nullable(Long.class), eq(updateHabitStatisticDto));
    }

    @Test
    void getTodayStatisticsForAllHabitItems() throws Exception {
        List<HabitItemsAmountStatisticDto> listHabitItemsAmountStatisticDto
                = Collections.singletonList(new HabitItemsAmountStatisticDto());
        Locale locale = Locale.ENGLISH;

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage()))
        .thenReturn(listHabitItemsAmountStatisticDto);
        mockMvc.perform(get(habitStatisticResource + "/todayStatisticsForAllHabitItems"))
                .andExpect(status().isOk());

        ResponseEntity<List<HabitItemsAmountStatisticDto>> response =
                habitStatisticController.getTodayStatisticsForAllHabitItems(locale);

        Assertions.assertEquals(listHabitItemsAmountStatisticDto, response.getBody());
    }

    @Test
    void findAmountOfAcquiredHabits() throws Exception {
        Long userId = ModelUtils.getUser().getId();
        Long amountOfAcquiredHabits = 10L;

        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId))
                .thenReturn(amountOfAcquiredHabits);
        mockMvc.perform(get(habitStatisticResource + "/acquired/count?userId="+ userId))
                .andExpect(status().isOk());

        ResponseEntity<Long> response =
                habitStatisticController.findAmountOfAcquiredHabits(userId);

        Assertions.assertEquals(amountOfAcquiredHabits, response.getBody());
    }

    @Test
    void findAmountOfHabitsInProgress() throws Exception {
        Long userId = ModelUtils.getUser().getId();
        Long amountOfHabitsInProgress = 10L;

        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId))
                .thenReturn(amountOfHabitsInProgress);
        mockMvc.perform(get(habitStatisticResource + "/acquired/count?userId="+ userId))
                .andExpect(status().isOk());

        ResponseEntity<Long> response =
                habitStatisticController.findAmountOfHabitsInProgress(userId);

        Assertions.assertEquals(amountOfHabitsInProgress, response.getBody());
    }
}
