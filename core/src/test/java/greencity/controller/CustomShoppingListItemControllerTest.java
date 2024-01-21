package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListItemControllerTest {
    private MockMvc mock;

    @Mock
    private CustomShoppingListItemService csliService;

    @InjectMocks
    private CustomShoppingListItemController csliController;

    private CustomShoppingListItemResponseDto csliResponseDto;
    private ObjectMapper objectMapper;
    private final Principal user = () -> "test@gmail.com";
    private static final String csliRoute = "/custom/shopping-list-items";


    @BeforeEach
    void initEach() {
        this.mock = MockMvcBuilders
                .standaloneSetup(csliController)
                .build();

        objectMapper = new ObjectMapper();

        csliResponseDto = new CustomShoppingListItemResponseDto(45L, "Happy Birthday !",
                ShoppingListItemStatus.ACTIVE);
    }

    @Test
    void getAllAvailableCustomShoppingListItems() throws Exception {
        Long userId = 1L;
        Long habitId = 1L;

        this.mock
                .perform(get(String.format("%s/%d/%d", csliRoute, userId, habitId))
                        .principal(user))
                .andExpect(status().isOk());

        Mockito.when(csliService.findAllAvailableCustomShoppingListItems(userId, habitId))
                .thenReturn(Collections.singletonList(csliResponseDto));

        Mockito.verify(csliService, Mockito.times(1))
                .findAllAvailableCustomShoppingListItems(userId, habitId);

        Assertions.assertEquals(csliResponseDto,
                csliController.getAllAvailableCustomShoppingListItems(userId, habitId).getBody().get(0));
    }

    @Test
    void saveUserCustomShoppingListItems() throws Exception {
        Long userId = 1L;
        Long habitAssignId = 1L;
        BulkSaveCustomShoppingListItemDto csliBulkSaveDto = new BulkSaveCustomShoppingListItemDto();
        String content = objectMapper.writeValueAsString(csliBulkSaveDto);

        this.mock
                .perform(post(
                        String.format("%s/%d/%d/custom-shopping-list-items", csliRoute, userId, habitAssignId))
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Mockito.when(csliService.save(csliBulkSaveDto, userId, habitAssignId))
                .thenReturn(Collections.singletonList(csliResponseDto));

        Mockito.verify(csliService, Mockito.times(1)).save(csliBulkSaveDto, userId, habitAssignId);

        Assertions.assertEquals(csliResponseDto,
                csliController.saveUserCustomShoppingListItems(csliBulkSaveDto, userId, habitAssignId).getBody().get(0));
    }

    @Test
    void updateItemStatus() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        String itemStatus = ShoppingListItemStatus.INPROGRESS.name();
        csliResponseDto = new CustomShoppingListItemResponseDto(45L, "Happy Birthday !",
                ShoppingListItemStatus.INPROGRESS);

        this.mock
                .perform(patch(String.format("%s/%d/custom-shopping-list-items", csliRoute, userId))
                        .param("itemId", itemId.toString())
                        .param("status", itemStatus)
                        .principal(user))
                .andExpect(status().isOk());

        Mockito.when(csliService.updateItemStatus(userId, itemId, itemStatus))
                .thenReturn(csliResponseDto);

        Mockito.verify(csliService, Mockito.times(1)).updateItemStatus(userId, itemId, itemStatus);

        Assertions.assertEquals(csliResponseDto.getStatus(),
                csliController.updateItemStatus(userId, itemId, itemStatus).getBody().getStatus());
    }

    @Test
    void updateItemStatusToDone() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        this.mock.perform(
                        patch(String.format("%s/%d/done", csliRoute, userId))
                                .param("itemId", itemId.toString())
                                .principal(user))
                .andExpect(status().isOk());

        Mockito.verify(csliService, Mockito.times(1)).updateItemStatusToDone(userId, itemId);
    }

    @Test
    void bulkDeleteCustomShoppingListItems() throws Exception {
        Long userId = 1L;
        String ids = "1,2,3";
        ArrayList<Long> answer = new ArrayList<>(Arrays.asList(1L, 2L, 3L));

        this.mock
                .perform(delete(String.format("%s/%d/custom-shopping-list-items", csliRoute, userId))
                        .param("ids", ids)
                        .principal(user))
                .andExpect(status().isOk());

        Mockito.when(csliService.bulkDelete(ids)).thenReturn(answer);

        Mockito.verify(csliService, Mockito.times(1)).bulkDelete(ids);

        Assertions.assertEquals(answer, csliController.bulkDeleteCustomShoppingListItems(ids, userId).getBody());
    }

    @Test
    void getAllCustomShoppingItemsByStatus() throws Exception {
        Long userId = 1L;
        String itemStatus = ShoppingListItemStatus.INPROGRESS.name();
        csliResponseDto = new CustomShoppingListItemResponseDto(
                45L,
                "Happy Birthday !",
                ShoppingListItemStatus.INPROGRESS);

        this.mock
                .perform(get(String.format("%s/%d/custom-shopping-list-items", csliRoute, userId))
                        .param("status", itemStatus)
                        .principal(user))
                .andExpect(status().isOk());

        Mockito.when(csliService.findAllUsersCustomShoppingListItemsByStatus(userId, itemStatus))
                .thenReturn(Collections.singletonList(csliResponseDto));

        Mockito.verify(csliService, Mockito.times(1))
                .findAllUsersCustomShoppingListItemsByStatus(userId, itemStatus);

        Assertions.assertEquals(csliResponseDto,
                csliController.getAllCustomShoppingItemsByStatus(userId, itemStatus).getBody().get(0));
    }

}
