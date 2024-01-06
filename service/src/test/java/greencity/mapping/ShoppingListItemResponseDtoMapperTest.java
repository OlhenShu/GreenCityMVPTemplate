package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static greencity.ModelUtils.getShoppingListItem;
import static greencity.ModelUtils.getShoppingListItemTranslations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ShoppingListItemResponseDtoMapperTest {
    @InjectMocks
    private ShoppingListItemResponseDtoMapper mapper;

    @Test
    void convert() {

        ShoppingListItem shoppingListItem = getShoppingListItem();

        List<ShoppingListItemTranslation> shoppingListItemTranslations = getShoppingListItemTranslations();

        List<ShoppingListItemTranslationDTO> shoppingListItemTranslationDTOS = shoppingListItemTranslations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        ShoppingListItemResponseDto expected = ShoppingListItemResponseDto.builder()
                .id(1L)
                .translations(shoppingListItemTranslationDTOS)
                .build();

        ShoppingListItemResponseDto actual = mapper.convert(shoppingListItem);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTranslations(), actual.getTranslations());
    }

    private ShoppingListItemTranslationDTO toDTO(ShoppingListItemTranslation itemTranslation) {

        return ShoppingListItemTranslationDTO.builder()
                .id(itemTranslation.getId())
                .content(itemTranslation.getContent())
                .build();
    }
}