package greencity.mapping;

import static greencity.ModelUtils.getFilter;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FilterDtoResponseMapperTest {

    @InjectMocks
    private FilterDtoResponseMapper filterDtoResponseMapper;

    @Test
    public void convertTest(){
        Filter filter = getFilter();
        String values = filter.getValues();
        String[] criterias = values.split(";");

        UserFilterDtoResponse expected
            = UserFilterDtoResponse.builder()
            .id(filter.getId())
            .name(filter.getName())
            .searchCriteria(criterias[0])
            .userRole(criterias[1])
            .userStatus(criterias[2])
            .build();

        assertEquals(expected,filterDtoResponseMapper.convert(filter));
    }
}
