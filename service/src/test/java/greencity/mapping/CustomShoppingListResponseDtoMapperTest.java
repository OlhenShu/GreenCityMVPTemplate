package greencity.mapping;

import static greencity.ModelUtils.getCustomShoppingListItem;
import static greencity.ModelUtils.getCustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CustomShoppingListResponseDtoMapperTest {

    @InjectMocks
    private CustomShoppingListResponseDtoMapper customShoppingListResponseDtoMapper;

    @Test
    public void convertTest() {
        CustomShoppingListItem customShoppingListItem = getCustomShoppingListItem();

        CustomShoppingListItemResponseDto expected =
            CustomShoppingListItemResponseDto.builder()
                .id(customShoppingListItem.getId())
                .text(customShoppingListItem.getText())
                .status(customShoppingListItem.getStatus())
                .build();

        assertEquals(expected, customShoppingListResponseDtoMapper.convert(customShoppingListItem));
    }

    @Test
    public void mapToAllListTest() {
        List<CustomShoppingListItem> itemList =
            Collections.singletonList(getCustomShoppingListItem());

        List<CustomShoppingListItemResponseDto> expected = itemList
            .stream()
            .map(customShoppingListItem -> CustomShoppingListItemResponseDto.builder()
                .id(customShoppingListItem.getId())
                .text(customShoppingListItem.getText())
                .status(customShoppingListItem.getStatus())
                .build())
            .collect(Collectors.toList());

        assertEquals(expected, customShoppingListResponseDtoMapper.mapAllToList(itemList));
    }
}
