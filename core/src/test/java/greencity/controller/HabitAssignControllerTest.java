package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.HabitAssignDto;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


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
    private Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void getHabitAssign() throws Exception {

        UserVO userVO = getUserVO();
        Locale locale = Locale.ENGLISH;
        Long habitAssignId = 1L;

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .id(habitAssignId)
                .build();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        when(habitAssignService.getByHabitAssignIdAndUserId(anyLong(), anyLong(), anyString()))
                .thenReturn(habitAssignDto);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(habitAssignDto);

        mockMvc.perform(get(link + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(eq(habitAssignId), eq(userVO.getId()), eq(locale.getLanguage()));
    }
}