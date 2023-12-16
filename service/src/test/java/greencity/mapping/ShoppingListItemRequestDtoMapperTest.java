package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getUserShoppingListItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ShoppingListItemRequestDtoMapperTest {
    @InjectMocks
    private ShoppingListItemRequestDtoMapper mapper;

    @Test
    void convert() {

        ShoppingListItemRequestDto shoppingListItemRequestDto =
                ShoppingListItemRequestDto.builder()
                        .id(1L)
                        .build();

        UserShoppingListItem expected = UserShoppingListItem.builder()
                .shoppingListItem(ShoppingListItem.builder().id(1L).build())
                .status(ShoppingListItemStatus.ACTIVE)
                .build();

        UserShoppingListItem actual = mapper.convert(shoppingListItemRequestDto);

        assertEquals(expected.getId(), actual.getId());
    }
}