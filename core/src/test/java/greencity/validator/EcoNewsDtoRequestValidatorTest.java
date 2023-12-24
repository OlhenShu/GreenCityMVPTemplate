package greencity.validator;

import greencity.constant.ValidationConstants;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Mock
    private UrlValidator urlValidator;

    @Test
    void isValidTrueTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("https://eco-lavca.ua/");
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidEmptySourceTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("");

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidNullSourceTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource(null);

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWrongCountOfTagsExceptionTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(Collections.nCopies(ValidationConstants.MAX_AMOUNT_OF_TAGS + 1, "tag"));

        assertThrows(WrongCountOfTagsException.class,
                () -> validator.isValid(request, null),
                "Expected WrongCountOfTagsException to be thrown");
    }

    @Test
    void isValidMaxCountOfTagsTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(Collections.nCopies(ValidationConstants.MAX_AMOUNT_OF_TAGS, "tag"));

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithValidTagsTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(Arrays.asList("News", "Events", "Initiatives"));

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithEmptyTagsTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(Collections.emptyList());

        assertThrows(WrongCountOfTagsException.class,
                () -> validator.isValid(request, null),
                "Expected WrongCountOfTagsException to be thrown");
    }

}
