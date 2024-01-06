package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemControllerTest {
    @Mock
    ShoppingListItemService shoppingListItemService;
    @Mock
    private UserService userService;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    ShoppingListItemController shoppingListItemController;
    private static final String shoppingListItemControllerLink = "/user/shopping-list-items";
    private MockMvc mockMvc;
    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void saveUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        String requestJson =
            "[\n" +
                "  {\n" +
                "    \"id\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3\n" +
                "  }\n" +
                "]\n";

        mockMvc.perform(post(shoppingListItemControllerLink)
                .principal(userVO::getEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .param("habitId", "1")
                .locale(locale))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        List<ShoppingListItemRequestDto> shoppingListItemRequestDtoList =
            Arrays.asList(mapper.readValue(requestJson, ShoppingListItemRequestDto[].class));

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).saveUserShoppingListItems(userVO.getId(),
            1L, shoppingListItemRequestDtoList, locale.getLanguage());
    }

    @Test
    void getUserShoppingListItemsWithLanguageParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(shoppingListItemControllerLink + "/habits/{habitId}/shopping-list", 1L)
                .principal(userVO::getEmail)
                .locale(locale))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), 1L, locale.getLanguage());
    }

    @Test
    void getUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(shoppingListItemControllerLink + "/habits/{habitId}/shopping-list", 1L)
                .principal(userVO::getEmail))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), 1L, Locale.getDefault().getLanguage());
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(shoppingListItemControllerLink)
                .principal(userVO::getEmail)
                .param("habitId", "1")
                .param("shoppingListItemId", "1"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, userVO.getId(), 1L);
    }

    @Test
    void updateUserShoppingListItemStatusWithoutStatusParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}", 1L)
                .principal(userVO::getEmail)
                .locale(locale))
            .andExpect(status().isCreated());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShopingListItemStatus(userVO.getId(), 1L, locale.getLanguage());
    }

    @Test
    void updateUserShoppingListItemStatusWithLanguageParamWithoutStatusParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}", 1L)
                .principal(userVO::getEmail)
                .locale(locale))
            .andExpect(status().isCreated());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShopingListItemStatus(userVO.getId(), 1L, locale.getLanguage());
    }

    @Test
    void updateUserShoppingListItemStatusWithoutLanguageParamWithoutStatusParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}", 1L)
                .principal(userVO::getEmail))
            .andExpect(status().isCreated());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShopingListItemStatus(userVO.getId(), 1L,
            Locale.getDefault().getLanguage());
    }

    @Test
    void updateUserShoppingListItemStatusWithStatusParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        ShoppingListItemStatus status = ShoppingListItemStatus.DISABLED;

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}/status/{status}",
                1L, status)
                .principal(userVO::getEmail)
                .locale(locale))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(),
            1L, locale.getLanguage(), status.toString());
    }

    @Test
    void updateUserShoppingListItemStatusWithLanguageParamWithStatusParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        ShoppingListItemStatus status = ShoppingListItemStatus.DISABLED;

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}/status/{status}",
                1L, status)
                .principal(userVO::getEmail)
                .locale(locale))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(),
            1L, locale.getLanguage(), status.toString());
    }

    @Test
    void updateUserShoppingListItemStatusWithoutLanguageWithStatusParamParamTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        ShoppingListItemStatus status = ShoppingListItemStatus.DISABLED;

        mockMvc.perform(patch(shoppingListItemControllerLink + "/{userShoppingListItemId}/status/{status}",
                1L, status)
                .principal(userVO::getEmail))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(),
            1L, Locale.getDefault().getLanguage(), status.toString());
    }

    @Test
    void bulkDeleteUserShoppingListItemTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        String ids = "1,2,3";

        mockMvc.perform(delete(shoppingListItemControllerLink + "/user-shopping-list-items")
                .principal(userVO::getEmail)
                .param("ids", ids))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(shoppingListItemService).deleteUserShoppingListItems(ids);
    }

    @Test
    void findInProgressByUserTest() throws Exception {
        UserVO userVO = getUserVO();

        mockMvc.perform(get(shoppingListItemControllerLink + "/{userId}/get-all-inprogress",
                userVO.getId())
                .principal(userVO::getEmail)
                .param("lang", locale.getLanguage()))
            .andExpect(status().isOk());

        verify(shoppingListItemService).findInProgressByUserIdAndLanguageCode(userVO.getId(), locale.getLanguage());
    }

}
