package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class HabitAssignControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitAssignService habitAssignService;

    @Mock
    UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    HabitAssignController habitAssignController;

    private static final String link = "/habit/assign";
    private final Principal principal = getPrincipal();
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserVO userVO = getUserVO();
    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
    }

    @Test
    void getHabitAssign() throws Exception {

        Long habitAssignId = 1L;

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .id(habitAssignId)
                .build();

        when(habitAssignService.getByHabitAssignIdAndUserId(anyLong(), anyLong(), anyString()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(get(link + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(
                eq(habitAssignId),
                eq(userVO.getId()),
                eq(locale.getLanguage()));
    }

    @Test
    void assignDefault() throws Exception {

        Long habitId = 1L;

        HabitAssignManagementDto managementDto = HabitAssignManagementDto.builder()
                .habitId(10L)
                .build();

        when(habitAssignService.assignDefaultHabitForUser(anyLong(), any()))
                .thenReturn(managementDto);

        mockMvc.perform(post(link + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(habitAssignService).assignDefaultHabitForUser(eq(habitId), eq(userVO));
    }

    @Test
    void assignCustom() throws Exception {

        Long habitId = 1L;

        HabitAssignCustomPropertiesDto customPropertiesDto = getHabitAssignCustomPropertiesDto();
        HabitAssignManagementDto managementDto = HabitAssignManagementDto.builder()
                .habitId(10L)
                .build();

        when(habitAssignService.assignCustomHabitForUser(anyLong(), any(), any()))
                .thenReturn(List.of(managementDto));

        mockMvc.perform(post(link + "/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(mapper.writeValueAsString(customPropertiesDto)))
                .andExpect(status().isCreated());

        verify(habitAssignService).assignCustomHabitForUser(eq(habitId), eq(userVO), eq(customPropertiesDto));
    }

    @Test
    void updateHabitAssignDuration() throws Exception {

        Long habitAssignId = 1L;
        Integer duration = 10;
        HabitAssignUserDurationDto durationDto = HabitAssignUserDurationDto.builder()
                .habitId(1L)
                .workingDays(5)
                .duration(10)
                .build();

        when(habitAssignService.updateUserHabitInfoDuration(anyLong(), anyLong(), anyInt()))
                .thenReturn(durationDto);

        mockMvc.perform(put(link + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("duration", String.valueOf(duration)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserHabitInfoDuration(eq(habitAssignId), eq(userVO.getId()), eq(duration));
    }
}