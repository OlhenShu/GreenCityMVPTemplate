package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

        Long habitAssignId = getHabitAssignId();

        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.getByHabitAssignIdAndUserId(anyLong(), anyLong(), anyString()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(get(link + "/{habitAssignId}", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(
                eq(habitAssignId),
                eq(userVO.getId()),
                eq(locale.getLanguage()));
    }

    @Test
    void assignDefault() throws Exception {

        Long habitId = getHabitAssignId();

        HabitAssignManagementDto managementDto = getManagementDto();

        when(habitAssignService.assignDefaultHabitForUser(anyLong(), any()))
                .thenReturn(managementDto);

        mockMvc.perform(post(link + "/{habitId}", habitId)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(habitAssignService).assignDefaultHabitForUser(eq(habitId), eq(userVO));
    }

    @Test
    void assignCustom() throws Exception {

        Long habitId = getHabitAssignId();

        HabitAssignCustomPropertiesDto customPropertiesDto = getHabitAssignCustomPropertiesDto();
        HabitAssignManagementDto managementDto = getManagementDto();

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

        Long habitAssignId = getHabitAssignId();
        Integer duration = 10;
        HabitAssignUserDurationDto durationDto = HabitAssignUserDurationDto.builder()
                .habitId(getHabitAssignId())
                .workingDays(5)
                .duration(10)
                .build();

        when(habitAssignService.updateUserHabitInfoDuration(anyLong(), anyLong(), anyInt()))
                .thenReturn(durationDto);

        mockMvc.perform(put(link + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .principal(principal)
                        .param("duration", String.valueOf(duration)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserHabitInfoDuration(eq(habitAssignId), eq(userVO.getId()), eq(duration));
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquired() throws Exception {

        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(anyLong(), anyString()))
                .thenReturn(List.of(habitAssignDto));

        mockMvc.perform(get(link + "/allForCurrentUser")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(eq(userVO.getId()), eq(locale.getLanguage()));
    }

    @Test
    void getUserShoppingAndCustomShoppingLists() throws Exception {

        Long habitAssignId = getHabitAssignId();
        UserShoppingAndCustomShoppingListsDto shoppingListsDto = UserShoppingAndCustomShoppingListsDto.builder()
                .customShoppingListItemDto(List.of(getCustomShoppingListItemResponseDto()))
                .userShoppingListItemDto(List.of())
                .build();

        when(habitAssignService.getUserShoppingAndCustomShoppingLists(anyLong(), anyLong(), anyString()))
                .thenReturn(shoppingListsDto);

        mockMvc.perform(get(link + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(eq(userVO.getId()), eq(habitAssignId), eq(locale.getLanguage()));
    }

    @Test
    void updateUserAndCustomShoppingLists() throws Exception {

        Long habitAssignId = getHabitAssignId();
        UserShoppingAndCustomShoppingListsDto listsDto = getUserShoppingAndCustomShoppingListsDto();

        doNothing().when(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(anyLong(), anyLong(), any(), anyString());

        mockMvc.perform(put(link + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(mapper.writeValueAsString(listsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(eq(userVO.getId()), eq(habitAssignId), eq(listsDto), eq(locale.getLanguage()));
    }

    @Test
    void getListOfUserAndCustomShoppingListsInProgress() throws Exception {

        when(habitAssignService.getListOfUserAndCustomShoppingListsWithStatusInprogress(anyLong(), anyString()))
                .thenReturn(List.of(getUserShoppingAndCustomShoppingListsDto()));

        mockMvc.perform(get(link + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(eq(userVO.getId()), eq(locale.getLanguage()));
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquired() throws Exception {

        Long habitId = getHabitAssignId();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(anyLong(), anyString()))
                .thenReturn(List.of(habitAssignDto));

        mockMvc.perform(get(link + "/{habitId}/all", habitId))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(eq(habitId), eq(locale.getLanguage()));
    }

    @Test
    void getHabitAssignByHabitId() throws Exception {

        Long habitId = getHabitAssignId();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.findHabitAssignByUserIdAndHabitId(anyLong(), anyLong(), anyString()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(get(link + "/{habitId}/active", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(eq(userVO.getId()), eq(habitId), eq(locale.getLanguage()));
    }

    @Test
    void getUsersHabitByHabitAssignId() throws Exception {

        Long habitAssignId = getHabitAssignId();
        HabitDto habitDto = HabitDto.builder()
                .id(10L)
                .build();

        when(habitAssignService.findHabitByUserIdAndHabitAssignId(anyLong(), anyLong(), anyString()))
                .thenReturn(habitDto);

        mockMvc.perform(get(link + "/{habitAssignId}/more", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(eq(userVO.getId()), eq(habitAssignId), eq(locale.getLanguage()));
    }

    @Test
    void updateAssignByHabitId() throws Exception {

        Long habitAssignId = getHabitAssignId();
        HabitAssignStatDto habitAssignStatDto = HabitAssignStatDto.builder()
                .status(HabitAssignStatus.INPROGRESS)
                .build();
        HabitAssignManagementDto managementDto = HabitAssignManagementDto.builder()
                .id(getHabitAssignId())
                .createDateTime(ZonedDateTime.now())
                .build();

        when(habitAssignService.updateStatusByHabitAssignId(anyLong(), any()))
                .thenReturn(managementDto);

        mockMvc.perform(patch(link + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(eq(habitAssignId), eq(habitAssignStatDto));
    }

    @Test
    void enrollHabit() throws Exception {

        Long habitAssignId = getHabitAssignId();
        LocalDate date = LocalDate.now();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.enrollHabit(anyLong(), anyLong(), any(), anyString()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(post(link + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .principal(principal)
                        .param("date", date.toString()))
                .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date), eq(locale.getLanguage()));
    }

    @Test
    void unenrollHabit() throws Exception {

        Long habitAssignId = getHabitAssignId();
        LocalDate date = LocalDate.now();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.unenrollHabit(anyLong(), anyLong(), any()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(post(link + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .principal(principal)
                        .param("date", date.toString()))
                .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(eq(habitAssignId), eq(userVO.getId()), eq(date));
    }

    @Test
    void getInprogressHabitAssignOnDate() throws Exception {

        LocalDate date = LocalDate.now();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.findInprogressHabitAssignsOnDate(anyLong(), any(), anyString()))
                .thenReturn(List.of(habitAssignDto));

        mockMvc.perform(get(link + "/active/{date}", date)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(eq(userVO.getId()), eq(date), eq(locale.getLanguage()));
    }

    @Test
    void getHabitAssignBetweenDates() throws Exception {

        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(2);
        HabitsDateEnrollmentDto dateEnrollmentDto = HabitsDateEnrollmentDto.builder()
                .enrollDate(LocalDate.now())
                .build();

        when(habitAssignService.findHabitAssignsBetweenDates(anyLong(), any(), any(), anyString()))
                .thenReturn(List.of(dateEnrollmentDto));

        mockMvc.perform(get(link + "/activity/{from}/to/{to}", from, to)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(eq(userVO.getId()), eq(from), eq(to), eq(locale.getLanguage()));
    }

    @Test
    void cancelHabitAssign() throws Exception {

        Long habitId = getHabitAssignId();
        HabitAssignDto habitAssignDto = getHabitAssignDto();

        when(habitAssignService.cancelHabitAssign(anyLong(), anyLong()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(patch(link + "/cancel/{habitId}", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(eq(habitId), eq(userVO.getId()));
    }

    @Test
    void deleteHabitAssign() throws Exception {

        Long habitAssignId = getHabitAssignId();

        doNothing().when(habitAssignService).deleteHabitAssign(anyLong(), anyLong());

        mockMvc.perform(delete(link + "/delete/{habitAssignId}", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(eq(habitAssignId), eq(userVO.getId()));
    }

    @Test
    void updateShoppingListStatus() throws Exception {

        UpdateUserShoppingListDto shoppingListDto = getUpdateUserShoppingListDto();

        doNothing().when(habitAssignService).updateUserShoppingListItem(any());

        mockMvc.perform(put(link + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shoppingListDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(eq(shoppingListDto));
    }

    @Test
    void updateProgressNotificationHasDisplayed() throws Exception {

        Long habitAssignId = getHabitAssignId();

        doNothing().when(habitAssignService).updateProgressNotificationHasDisplayed(anyLong(), anyLong());

        mockMvc.perform(put(link + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(eq(habitAssignId), eq(userVO.getId()));
    }

    private static long getHabitAssignId() {

        return 1L;
    }

    private static HabitAssignManagementDto getManagementDto() {

        return HabitAssignManagementDto.builder()
                .habitId(10L)
                .build();
    }

    private static HabitAssignDto getHabitAssignDto() {

        return HabitAssignDto.builder()
                .id(getHabitAssignId())
                .build();
    }
}