package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getShoppingListItemTranslations;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingListItemDtoMapperTest {

    private final ShoppingListItemDtoMapper mapper = new ShoppingListItemDtoMapper();

    @Test
    void convert() {

        ShoppingListItemTranslation shoppingListItemTranslation = getShoppingListItemTranslations().get(0);
        ShoppingListItemDto expected = ShoppingListItemDto.builder()
                .id(shoppingListItemTranslation.getShoppingListItem().getId())
                .text(shoppingListItemTranslation.getContent())
                .status(ShoppingListItemStatus.ACTIVE.name())
                .build();

        ShoppingListItemDto actual = mapper.convert(shoppingListItemTranslation);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getText(), actual.getText());
    }
}