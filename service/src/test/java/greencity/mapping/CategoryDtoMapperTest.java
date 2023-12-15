package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CategoryDtoMapperTest {
    @InjectMocks
    private CategoryDtoMapper categoryDtoMapper;

    @Test
    void convertTest() {
        CategoryDto categoryDto = new CategoryDto("New Category");

        Category expected = Category
            .builder()
                .name(categoryDto.getName())
                    .build();


        assertEquals(expected, categoryDtoMapper.convert(categoryDto));
    }
}
