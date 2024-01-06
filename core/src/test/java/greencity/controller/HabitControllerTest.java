package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.ModelUtils.*;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EcoNewsService;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import java.util.Locale;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {
    private static final String habitsLink = "/habit";
    @Mock
    HabitService habitService;
    @Mock
    TagsService tagsService;
    @InjectMocks
    HabitController habitController;
    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    private Locale locale = Locale.ENGLISH;

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    public void getHabitByIdTest() throws Exception {
        Long id = 1L;

        mockMvc
            .perform(
                get(habitsLink + "/{id}", id)
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(habitService).getByIdAndLanguageCode(id, locale.getLanguage());
    }

    @Test
    public void getAllTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc
            .perform(
                get(habitsLink)
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(pageSize))
                    .locale(locale)
                    .principal(userVO::getEmail)
            )
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(habitService).getAllHabitsByLanguageCode(userVO, pageable, locale.getLanguage());
    }

    @Test
    public void getShoppingListItemsTest() throws Exception {
        Long id = 1L;

        mockMvc
            .perform(
                get(habitsLink + "/{id}/shopping-list", id)
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(habitService).getShoppingListForHabit(id, locale.getLanguage());
    }

    @Test
    public void getAllByTagsAndLanguageCodeTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");

        mockMvc
            .perform(
                get(habitsLink + "/tags/search")
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(pageSize))
                    .param("tags", tags.toArray(String[]::new)
                    )
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, locale.getLanguage());
    }

    @Test
    public void getAllByDifferentParametersTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        boolean isCustomHabit = true;
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");
        List<Integer> complexities = Collections.singletonList(1);

        mockMvc
            .perform(
                get(habitsLink + "/search")
                    .param("complexities", complexities.stream().map(String::valueOf).toArray(String[]::new))
                    .param("isCustomHabit", String.valueOf(isCustomHabit))
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(pageSize))
                    .param("tags", tags.toArray(String[]::new))
                    .principal(userVO::getEmail)
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(habitService).getAllByDifferentParameters(userVO, pageable, Optional.of(tags),
            Optional.of(isCustomHabit), Optional.of(complexities), locale.getLanguage());
    }

    @Test
    public void getAllByDifferentParametersBadRequestTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc
            .perform(
                get(habitsLink + "/search")
                    .principal(userVO::getEmail)
                    .locale(locale)
            )
            .andExpect(status().isBadRequest());

        verify(userService).findByEmail(userVO.getEmail());
    }

    @Test
    public void findAllHabitsTagsTest() throws Exception {
        mockMvc
            .perform(
                get(habitsLink + "/tags")
                    .locale(locale)
            )
            .andExpect(status().isOk());

        verify(tagsService).findAllHabitsTags(locale.getLanguage());
    }


    @Test
    public void addCustomHabitTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("John.Doe@gmail.com");
        String jsonRequest = new ObjectMapper().writeValueAsString(getAddCustomHabitDtoRequest());

        MockMultipartFile jsonFile =
            new MockMultipartFile("request", "", "application/json", jsonRequest.getBytes());


        mockMvc.perform(
                MockMvcRequestBuilders.multipart(habitsLink + "/custom")
                    .file(jsonFile)
                    .principal(principal)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated());


        ObjectMapper mapper = new ObjectMapper();
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = mapper.readValue(jsonRequest, AddCustomHabitDtoRequest.class);

        verify(habitService).addCustomHabit(eq(addCustomHabitDtoRequest),isNull(),eq("John.Doe@gmail.com"));
    }

    @Test
    public void getFriendsAssignedToHabitProfilePicturesTest() throws Exception{
        Long habitId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc
            .perform(
                get(habitsLink + "/{habitId}/friends/profile-pictures", habitId)
                    .principal(userVO::getEmail)
            )
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(habitService).getFriendsAssignedToHabitProfilePictures(habitId, userVO.getId());
    }
}
