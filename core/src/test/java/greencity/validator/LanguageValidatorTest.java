package greencity.validator;

import greencity.service.LanguageService;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LanguageValidatorTest {
    @InjectMocks
    private LanguageValidator languageValidator;
    @Mock
    private LanguageService languageService;

    @BeforeEach
    private void initialiseLanguageServiceMock() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en", "ua"));

        languageValidator.initialize(null);
    }

    @Test
    void initializeTest() {
        verify(languageService).findAllLanguageCodes();
    }

    @Test
    void isValidTrueTest() {
        assertTrue(languageValidator.isValid(Locale.ENGLISH, null));
        assertTrue(languageValidator.isValid(Locale.forLanguageTag("ua"), null));
    }

    @Test
    void isValidFalseTest() {
        Assertions.assertFalse(languageValidator.isValid(Locale.forLanguageTag("uu"), null));
    }
}
