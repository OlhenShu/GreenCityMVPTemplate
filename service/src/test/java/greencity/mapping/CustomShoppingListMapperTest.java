package greencity.mapping;

import greencity.ModelUtils;
import static greencity.ModelUtils.getCustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CustomShoppingListMapperTest {

    @InjectMocks
    private CustomShoppingListMapper customShoppingListMapper;

    @Test
    public void convertTest() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = getCustomShoppingListItemResponseDto();

        CustomShoppingListItem expected =
            CustomShoppingListItem.builder()
                .id(customShoppingListItemResponseDto.getId())
                .text(customShoppingListItemResponseDto.getText())
                .status(customShoppingListItemResponseDto.getStatus())
                .build();

        assertEquals(expected, customShoppingListMapper.convert(customShoppingListItemResponseDto));
    }

    @Test
    public void mapToAllListTest() {
        List<CustomShoppingListItemResponseDto> dtoList =
            Collections.singletonList(getCustomShoppingListItemResponseDto());

        List<CustomShoppingListItem> expected = dtoList
            .stream()
            .map(customShoppingListItemResponseDto -> CustomShoppingListItem.builder()
                .id(customShoppingListItemResponseDto.getId())
                .text(customShoppingListItemResponseDto.getText())
                .status(customShoppingListItemResponseDto.getStatus())
                .build())
            .collect(Collectors.toList());

        assertEquals(expected,customShoppingListMapper.mapAllToList(dtoList));
    }
}
