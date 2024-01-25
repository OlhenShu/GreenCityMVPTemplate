package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.shoppinglistitem.*;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.HabitAssign;
import greencity.entity.Language;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.*;
import greencity.filters.ShoppingListItemSpecification;
import greencity.repository.HabitAssignRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.UserShoppingListItemRepo;
import greencity.service.ShoppingListItemServiceImpl;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShoppingListItemServiceImplTest {
    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserShoppingListItemRepo userShoppingListItemRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;
    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;
    @InjectMocks
    private ShoppingListItemServiceImpl shoppingListItemService;
    private final String language = getLanguageUa().getCode();

    private final ShoppingListItem shoppingListItem = getShoppingListItem();

    private final  ShoppingListItemTranslation shoppingListItemTranslation = ShoppingListItemTranslation.builder()
            .id(1L)
            .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList()))
            .content("TEST")
            .shoppingListItem(
                    new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .build();

    private final List<ShoppingListItemTranslation> shoppingListItemTranslations = Arrays.asList(
            ShoppingListItemTranslation.builder()
                    .id(1L)
                    .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList()))
                    .content("TEST")
                    .shoppingListItem(
                            new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                    .build(),
            ShoppingListItemTranslation.builder()
                    .id(2L)
                    .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList()))
                    .content("TEST")
                    .shoppingListItem(
                            new ShoppingListItem(2L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                    .build());

    private final ShoppingListItemRequestDto shoppingListItemRequestDto = new ShoppingListItemRequestDto(1L);

    private final ShoppingListItemPostDto shoppingListItemPostDto =
            new ShoppingListItemPostDto(getLanguageTranslationsDTOs(), new ShoppingListItemRequestDto(1L));

    List<ShoppingListItemRequestDto> shoppingListItemRequestDtos =
            Arrays.asList(new ShoppingListItemRequestDto(1L), new ShoppingListItemRequestDto(2L),
                    new ShoppingListItemRequestDto(3L));



    @Test
    void findAll() {
        ShoppingListItemDto shoppingListItemDto = ShoppingListItemDto.builder().id(1L).text("test").status("status").build();
        when(shoppingListItemTranslationRepo.findAllByLanguageCode(language)).thenReturn(shoppingListItemTranslations);
        when(modelMapper.map(shoppingListItemTranslations.get(0), ShoppingListItemDto.class)).thenReturn(shoppingListItemDto);
        when(modelMapper.map(shoppingListItemTranslations.get(1), ShoppingListItemDto.class)).thenReturn(shoppingListItemDto);
        List<ShoppingListItemDto> actual = shoppingListItemService.findAll(language);
        assertEquals(List.of(shoppingListItemDto, shoppingListItemDto), actual);

    }

    @Test
    void saveUserShoppingListItemThrowException() {

        List<ShoppingListItemRequestDto> shoppingListItemRequestDto =
                Collections.singletonList(shoppingListItemRequestDtos.get(0));

        assertThrows(UserHasNoShoppingListItemsException.class, () -> shoppingListItemService
                .saveUserShoppingListItems(1L, 1L, shoppingListItemRequestDto, "en"));
    }

    @Test
    void saveShoppingListItem() {
        LanguageTranslationDTO languageTranslationDTO = getLanguageTranslationDTO();
        ShoppingListItemPostDto shoppingListItemPostDto = new ShoppingListItemPostDto(List.of(languageTranslationDTO), shoppingListItemRequestDto);
        when(shoppingListItemRepo.save(any(ShoppingListItem.class))).thenReturn(shoppingListItem);
        when(modelMapper.map(shoppingListItemPostDto, ShoppingListItem.class)).thenReturn(shoppingListItem);
         when(modelMapper.map(shoppingListItem.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType())).thenReturn(List.of(languageTranslationDTO));
        List<LanguageTranslationDTO> actual = shoppingListItemService.saveShoppingListItem(shoppingListItemPostDto);
        assertEquals(List.of(languageTranslationDTO), actual);
    }

    @Test
    void update() {
        List<LanguageTranslationDTO> languageTranslationDTO = List.of(getLanguageTranslationDTO());
        when(shoppingListItemRepo.findById(shoppingListItemPostDto.getShoppingListItem().getId()))
                .thenReturn(Optional.of(shoppingListItem));
        when(modelMapper.map(shoppingListItem.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType())).thenReturn(languageTranslationDTO);
        List<LanguageTranslationDTO> res = shoppingListItemService.update(shoppingListItemPostDto);
        assertEquals(languageTranslationDTO.get(0).getContent(), res.get(0).getContent());
    }

    @Test
    void updateUserShoppingListItemStatusShouldThrowNotFoundExceptionTest() {
        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(999L, 999L))
                .thenReturn(null);

        Exception thrown = assertThrows(NotFoundException.class, () -> shoppingListItemService
                .updateUserShoppingListItemStatus(999L, 999L, "ua", "DONE"));

        assertEquals(ErrorMessage.USER_SHOPPING_LIST_ITEM_NOT_FOUND_BY_USER_ID, thrown.getMessage());
        verify(userShoppingListItemRepo).getAllByUserShoppingListIdAndUserId(999L, 999L);
    }

    @Test
    void updateUserShoppingListItemStatusShouldThrowBadRequestExceptionTest() {
        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(999L, 999L))
                .thenReturn(List.of(ModelUtils.getPredefinedUserShoppingListItem()));

        Exception thrown = assertThrows(BadRequestException.class, () -> shoppingListItemService
                .updateUserShoppingListItemStatus(999L, 999L, "ua", "Wrong Status"));

        assertEquals(ErrorMessage.INCORRECT_INPUT_ITEM_STATUS, thrown.getMessage());
        verify(userShoppingListItemRepo).getAllByUserShoppingListIdAndUserId(999L, 999L);
    }

    @Test
    void findShoppingListItemById() {
        when(shoppingListItemRepo.findById(1L)).thenReturn(Optional.of(shoppingListItem));
        when(modelMapper.map(shoppingListItem, ShoppingListItemResponseDto.class)).thenReturn(getShoppingListItemResponseDto());
        ShoppingListItemResponseDto actual = shoppingListItemService.findShoppingListItemById(1L);
        assertEquals(getShoppingListItemResponseDto(), actual);
    }

    @Test
    void delete() {
        shoppingListItemService.delete(1L);
        verify(shoppingListItemRepo).deleteById(1L);
    }

    @Test
    void findShoppingListItemsForManagementByPage() {
        PageRequest request = PageRequest.of(0, 1);
        List<ShoppingListItem> shoppingListItems = Collections.singletonList(shoppingListItem);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, request, shoppingListItems.size());
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto
                .builder().translations(getUserShoppingListItemVO().getShoppingListItemVO().getTranslations()).build();
        when(shoppingListItemRepo.findAll(request)).thenReturn(page);
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class)).thenReturn(shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> actual
                = shoppingListItemService.findShoppingListItemsForManagementByPage(request);
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(List.of(shoppingListItemManagementDto), List.of(shoppingListItemManagementDto).size(),
                0, 1, 0, false, false, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void deleteAllShoppingListItemsByListOfId() {
        List<Long> longs = List.of(1L, 2L);
        List<Long> actual = shoppingListItemService.deleteAllShoppingListItemsByListOfId(longs);
        assertEquals(longs, actual);
    }

    @Test
    void searchBy() {
        PageRequest request = PageRequest.of(0, 1);
        List<ShoppingListItem> shoppingListItems = Collections.singletonList(shoppingListItem);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, request, shoppingListItems.size());
        when(shoppingListItemRepo.searchBy(request, "test")).thenReturn(page);
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto
                .builder().translations(getUserShoppingListItemVO().getShoppingListItemVO().getTranslations()).build();
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class)).thenReturn(shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> actual = shoppingListItemService.searchBy(request, "test");
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(List.of(shoppingListItemManagementDto), List.of(shoppingListItemManagementDto).size(),
                0, 1, 0, false, false, true, true);
        assertEquals(expected, actual);

    }

    @Test
    void getFilteredDataForManagementByPage() {
        PageRequest request = PageRequest.of(0, 1);
        List<ShoppingListItem> shoppingListItems = Collections.singletonList(shoppingListItem);
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, request, shoppingListItems.size());
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto
                .builder().translations(getUserShoppingListItemVO().getShoppingListItemVO().getTranslations()).build();
        when(shoppingListItemRepo.findAll(any(ShoppingListItemSpecification.class), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class)).thenReturn(shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> actual = shoppingListItemService
                .getFilteredDataForManagementByPage(request, ShoppingListItemViewDto.builder().id("1L").content("test").build());
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(List.of(shoppingListItemManagementDto), List.of(shoppingListItemManagementDto).size(),
                0, 1, 0, false, false, true, true);
        assertEquals(expected, actual);
    }

    @Test
    void saveUserShoppingListItems() {
        List<ShoppingListItemRequestDto> dtoList = null;
        Long userId = 1L;
        Long habitId = 1L;
        String language = "en";
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        UserShoppingListItem userShoppingListItem =
                UserShoppingListItem.builder().id(1L).status(ShoppingListItemStatus.ACTIVE).build();

        List<UserShoppingListItemResponseDto> expected =
                List.of(ModelUtils.getUserShoppingListItemResponseDto());

        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId))
                .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.singletonList(
                userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(expected.get(0));
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, 1L))
                .thenReturn(ShoppingListItemTranslation.builder().id(1L).build());

        List<UserShoppingListItemResponseDto> actual = shoppingListItemService
                .saveUserShoppingListItems(userId, habitId, dtoList, language);

        assertEquals(expected, actual);

    }

    @Test
    void getUserShoppingList() {
        HabitAssign habitAssign = getHabitAssign();
        UserShoppingListItem userShoppingListItem =
                UserShoppingListItem.builder().id(1L).status(ShoppingListItemStatus.ACTIVE).build();
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssign.getId()))
                .thenReturn(List.of(userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(UserShoppingListItemResponseDto.builder().id(1L).build());
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
                .thenReturn(ShoppingListItemTranslation.builder().id(1L).build());
        assertEquals(1L, shoppingListItemService.getUserShoppingList( 1L, 1L, "en").get(0).getId());

    }

    @Test
    void findShoppingListItemByIdTestFailed() {
        assertThrows(ShoppingListItemNotFoundException.class,
                () -> shoppingListItemService.findShoppingListItemById(30L));
    }

    @Test
    void getUserShoppingListByHabitAssignId() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        when(habitAssignRepo.findById(1L))
                .thenReturn(Optional.of(getHabitAssign()));
        when(userShoppingListItemRepo.findAllByHabitAssingId(1L)).thenReturn(Collections.singletonList(
                userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(getUserShoppingListItemResponseDto());
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, 1L))
                .thenReturn(shoppingListItemTranslation);

        List<UserShoppingListItemResponseDto> actualDtoList = shoppingListItemService
                .getUserShoppingListByHabitAssignId(1L, 1L, language);

        assertEquals(List.of(getUserShoppingListItemResponseDto()), actualDtoList);
    }

    @Test
    @Order(20)
    void getUserShoppingListItemsByHabitAssignIdAndStatusInProgress() {
        UserShoppingListItem item = getUserShoppingListItem();

        UserShoppingListItemResponseDto userShoppingListItemResponseDto = getUserShoppingListItemResponseDto();
        when(userShoppingListItemRepo.findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(1L))
                .thenReturn(List.of(item));
        when(modelMapper.map(item, UserShoppingListItemResponseDto.class))
                .thenReturn(userShoppingListItemResponseDto);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
                .thenReturn(ShoppingListItemTranslation.builder().id(1L).build());
        List<UserShoppingListItem> userShoppingListItem = List.of(item);
        when(userShoppingListItemRepo.saveAll(any())).thenReturn(userShoppingListItem);


        List<UserShoppingListItemResponseDto> actual =
                shoppingListItemService.
                        getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(1L, "en");

        assertEquals(List.of(userShoppingListItemResponseDto), actual);

    }

    @Test
    void deleteUserShoppingListItemByItemIdAndUserIdAndHabitId() {
        userShoppingListItemRepo.deleteByShoppingListItemIdAndHabitAssignId(1L, 1L);
        verify(userShoppingListItemRepo).deleteByShoppingListItemIdAndHabitAssignId(1L, 1L);
    }

    @Test
    void updateUserShopingListItemStatus() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        userShoppingListItem.setStatus(ShoppingListItemStatus.ACTIVE);
        when(userShoppingListItemRepo.getOne(1L)).thenReturn(userShoppingListItem);
        when(userShoppingListItemRepo.save(userShoppingListItem)).thenReturn(userShoppingListItem);
        UserShoppingListItemResponseDto userShoppingListItemResponseDto = getUserShoppingListItemResponseDto();
        userShoppingListItemResponseDto.setStatus(ShoppingListItemStatus.ACTIVE);
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(userShoppingListItemResponseDto);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
                .thenReturn(getShoppingListItemTranslations().get(0));

        UserShoppingListItemResponseDto actual =
                shoppingListItemService.updateUserShopingListItemStatus(1L, 1L, "en");
        assertEquals(userShoppingListItemResponseDto, actual);

    }

    @Test
    void updateUserShoppingListItemStatus() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(1L, 1L))
                .thenReturn(List.of(userShoppingListItem));
        when(modelMapper.map(userShoppingListItem, UserShoppingListItemResponseDto.class))
                .thenReturn(UserShoppingListItemResponseDto.builder()
                        .id(1L)
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build());
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId("en", 1L))
                .thenReturn(getShoppingListItemTranslations().get(0));

        List<UserShoppingListItemResponseDto> result = shoppingListItemService
                .updateUserShoppingListItemStatus(1L, 1L, "en", "INPROGRESS");

        assertEquals(ShoppingListItemStatus.INPROGRESS, result.get(0).getStatus());
    }

    @Test
    void deleteUserShoppingListItems() {
        UserShoppingListItem userShoppingListItem = getUserShoppingListItem();
        when(userShoppingListItemRepo.findById(anyLong())).thenReturn(Optional.of(userShoppingListItem));
        List<Long> actual = shoppingListItemService.deleteUserShoppingListItems("1,2");
        assertEquals(Arrays.asList(1L, 2L), actual);
    }

    @Test
    void deleteTestFailed() {
        doThrow(EmptyResultDataAccessException.class).when(shoppingListItemRepo).deleteById(999L);

        assertThrows(NotDeletedException.class, () -> shoppingListItemService.delete(999L));
    }

    @Test
    void getShoppingListByHabitId() {
        List<Long> longs = List.of(1L);
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto.builder()
                .id(1L)
                .build();
        List<ShoppingListItemManagementDto> shoppingListItemManagementDtos =
                Collections.singletonList(shoppingListItemManagementDto);
        when(shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(anyLong())).thenReturn(longs);
        when(shoppingListItemRepo.getShoppingListByListOfId(longs)).thenReturn(List.of(getShoppingListItem()));
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class))
                .thenReturn(shoppingListItemManagementDto);
        List<ShoppingListItemManagementDto> actual = shoppingListItemService.getShoppingListByHabitId(1L);
        assertEquals(shoppingListItemManagementDtos, actual);

    }

    @Test
    void findAllShoppingListItemsForManagementPageNotContained() {
        PageRequest request = PageRequest.of(0, 1);
        List<Long> longs = List.of(1L);
        List<ShoppingListItem> shoppingListItems = List.of(getShoppingListItem());
        Page<ShoppingListItem> page = new PageImpl<>(shoppingListItems, request, shoppingListItems.size());
        ShoppingListItemManagementDto shoppingListItemManagementDto = ShoppingListItemManagementDto.builder()
                .id(1L)
                .build();
        List<ShoppingListItemManagementDto> dtoList = Collections.singletonList(shoppingListItemManagementDto);
        when(shoppingListItemRepo.getAllShoppingListItemsByHabitIdNotContained(1L))
                .thenReturn(longs);
        when(shoppingListItemRepo.getShoppingListByListOfIdPageable(longs, request))
                .thenReturn(page);
        when(modelMapper.map(shoppingListItem, ShoppingListItemManagementDto.class))
                .thenReturn(shoppingListItemManagementDto);
        PageableAdvancedDto<ShoppingListItemManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
                0, 1, 0, false, false, true, true);
        PageableAdvancedDto<ShoppingListItemManagementDto> actual =
                shoppingListItemService.findAllShoppingListItemsForManagementPageNotContained(1L, request);
        assertEquals(expected, actual);
    }

    @Test
    void updateThrowsTest() {
        assertThrows(ShoppingListItemNotFoundException.class,
                () -> shoppingListItemService.update(shoppingListItemPostDto));
    }

    @Test
    void updateUserShoppingListItemStatusWithNonExistentItemIdTest() {
        assertThrows(NullPointerException.class, () -> shoppingListItemService
                .updateUserShopingListItemStatus(999L, 999L, "en"));
    }

    @Test
    void findInProgressByUserIdAndLanguageCode() {
        List<ShoppingListItemTranslation> listItemTranslations = getShoppingListItemTranslations();


        when(shoppingListItemRepo.findInProgressByUserIdAndLanguageCode(1L, "en"))
                .thenReturn(listItemTranslations);
        when(modelMapper.map(listItemTranslations.get(0), ShoppingListItemDto.class))
                .thenReturn(new ShoppingListItemDto(1L, "", "DONE"));
        when(modelMapper.map(listItemTranslations.get(1), ShoppingListItemDto.class))
                .thenReturn(new ShoppingListItemDto(2L, "", "DONE"));

        List<ShoppingListItemDto> actual = shoppingListItemService.findInProgressByUserIdAndLanguageCode(1L, "en");
        List<ShoppingListItemDto> expected = Arrays.asList(
                new ShoppingListItemDto(1L, "","INPROGRESS"),
                new ShoppingListItemDto(2L, "","INPROGRESS")
        );
        assertEquals(expected, actual);

    }
}