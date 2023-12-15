package greencity.mapping;

import static greencity.ModelUtils.getUser;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsAuthorDtoMapperTest {

    @InjectMocks
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;

    @Test
    public void convertTest(){
        User author = getUser();

        EcoNewsAuthorDto expected =
            EcoNewsAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build();

        assertEquals(expected,ecoNewsAuthorDtoMapper.convert(author));
    }
}
