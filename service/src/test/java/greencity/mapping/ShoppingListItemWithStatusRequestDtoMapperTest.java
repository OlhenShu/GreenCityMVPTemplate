package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getUserShoppingListItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ShoppingListItemWithStatusRequestDtoMapperTest {

    @InjectMocks
    private ShoppingListItemWithStatusRequestDtoMapper mapper;

    @Test
    void convert() {

        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .build();

        UserShoppingListItem expected = getUserShoppingListItem();
        UserShoppingListItem actual = mapper.convert(requestDto);

        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getShoppingListItem(), actual.getShoppingListItem());
    }
}