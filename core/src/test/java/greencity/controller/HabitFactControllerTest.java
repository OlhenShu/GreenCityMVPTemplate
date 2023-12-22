package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationUpdateDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.enums.FactOfDayStatus;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {
    private MockMvc mockMvc;
    @Mock
    private HabitFactService habitFactService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private Validator mockValidator;
    @InjectMocks
    private HabitFactController habitFactController;

    private final String MOCK_URL = "/facts";
    List<HabitFactTranslationUpdateDto> dto = Arrays.asList(
            new HabitFactTranslationUpdateDto(FactOfDayStatus.POTENTIAL,
                    new LanguageDTO(1L, "ua"), "українська"),
            new HabitFactTranslationUpdateDto(FactOfDayStatus.POTENTIAL,
                    new LanguageDTO(2L, "en"), "english")
    );
    HabitFactUpdateDto habitFactUpdateDto = new HabitFactUpdateDto(dto,
            new HabitIdRequestDto(1L));

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
    }

    @Test
    @DisplayName("Get random fact about habit by requesting with habit Id")
    void testGetRandomFactByHabitId() throws Exception {
        Long habitId = 1L;
        Locale locale = new Locale("en");

        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, locale.getLanguage()))
                .thenReturn(new LanguageTranslationDTO());

        mockMvc.perform(get(MOCK_URL + "/random/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").hasJsonPath());

        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(any(), any());
    }

    @Test
    @DisplayName("Get habit fact of the day with specified translations")
    void testGetHabitFactOfTheDay() throws Exception {
        Long languageId = 1L;

        when(habitFactService.getHabitFactOfTheDay(languageId)).thenReturn(new LanguageTranslationDTO());

        mockMvc.perform(get(MOCK_URL + "/dayFact/{languageId}", languageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").hasJsonPath());


        verify(habitFactService).getHabitFactOfTheDay(languageId);
    }

    @Test
    @DisplayName("Get all facts about habit at the page")
    void testGetAll() throws Exception {
        when(habitFactService.getAllHabitFacts(any(Pageable.class), any(String.class)))
                .thenReturn(new PageableDto<>(new ArrayList<>(), 0, 0, 0));

        mockMvc.perform(get(MOCK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").hasJsonPath());

        verify(habitFactService).getAllHabitFacts(any(Pageable.class), any(String.class));
    }

    @Test
    @DisplayName("Trying to save new fact about habit")
    void testSave() throws Exception {
        when(habitFactService.save(any(HabitFactPostDto.class))).thenReturn(new HabitFactVO());

        var objectMapper = new ObjectMapper();

        mockMvc.perform(post(MOCK_URL)
                        .content(objectMapper.writeValueAsString(habitFactUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(habitFactService).save(any(HabitFactPostDto.class));
    }

    @Test
    @DisplayName("Updating facts translation about habit")
    void testUpdate() throws Exception {
        when(habitFactService.update(any(HabitFactUpdateDto.class), any(Long.class)))
                .thenReturn(new HabitFactVO());

        var objectMapper = new ObjectMapper();

        mockMvc.perform(put(MOCK_URL + "/{id}", 1)
                        .content(objectMapper.writeValueAsString(habitFactUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitFactService).update(any(HabitFactUpdateDto.class), anyLong());

    }

    @Test
    @DisplayName("Deleting fact about habit")
    void testDelete() throws Exception {
        Long factId = 1L;

        when(habitFactService.delete(factId)).thenReturn(factId);

        mockMvc.perform(delete(MOCK_URL + "/{factId}", factId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitFactService).delete(factId);
    }

    @Test
    @DisplayName("Trying to delete fact about habit with incorrect value passed")
    void testDeleteInvalid() throws Exception {
        mockMvc.perform(delete(MOCK_URL + "/{factId}", "incorrectValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(habitFactService, times(0)).delete(anyLong());
    }
}