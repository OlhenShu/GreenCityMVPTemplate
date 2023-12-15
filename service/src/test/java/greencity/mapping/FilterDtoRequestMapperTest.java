package greencity.mapping;

import static greencity.ModelUtils.getUserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FilterDtoRequestMapperTest {

    @InjectMocks
    private FilterDtoRequestMapper filterDtoRequestMapper;

    @Test
    public void convertTest() {
        UserFilterDtoRequest filterUserDto = getUserFilterDtoRequest();

        StringBuilder values = new StringBuilder(filterUserDto.getSearchCriteria())
            .append(";")
            .append(filterUserDto.getUserRole())
            .append(";")
            .append(filterUserDto.getUserStatus());
        Filter expected = Filter.builder()
            .name(filterUserDto.getName())
            .type(FilterType.USERS.toString())
            .values(values.toString())
            .build();

        assertEquals(expected, filterDtoRequestMapper.convert(filterUserDto));
    }
}
