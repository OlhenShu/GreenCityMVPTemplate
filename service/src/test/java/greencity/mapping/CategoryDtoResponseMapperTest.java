package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CategoryDtoResponseMapperTest {

    @InjectMocks
    private CategoryDtoResponseMapper categoryDtoResponseMapper;

    @Test
    void convertTest() {
        Category category = Category.builder()
            .id(1L)
            .name("New Category")
            .build();

        CategoryDtoResponse expected = CategoryDtoResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .build();

        assertEquals(expected, categoryDtoResponseMapper.convert(category));
    }
}
