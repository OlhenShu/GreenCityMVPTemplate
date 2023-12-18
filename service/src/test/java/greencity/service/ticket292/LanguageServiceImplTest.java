package greencity.service.ticket292;


import greencity.constant.AppConstant;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import greencity.service.LanguageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {


    @Mock
    private LanguageRepo languageRepo;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    public void getAllLanguages_shouldReturnMappedLanguages() {
        List<Language> mockLanguages = Arrays.asList(new Language(), new Language());
        Mockito.when(languageRepo.findAll()).thenReturn(mockLanguages);
        List<LanguageDTO> actualDTOs = languageService.getAllLanguages();

        List<LanguageDTO> expectedDTOs = modelMapper.map(mockLanguages, new TypeToken<List<LanguageDTO>>() {
        }.getType());
        assertEquals(expectedDTOs, actualDTOs);
    }

    @Test
    public void findAllLanguageCodes_shouldReturnCodesFromRepo() {
        List<String> expectedCodes = Arrays.asList("en", "ua");
        Mockito.when(languageRepo.findAllLanguageCodes()).thenReturn(expectedCodes);

        List<String> actualCodes = languageService.findAllLanguageCodes();

        assertEquals(expectedCodes, actualCodes);
    }

    @Test
    void testExtractLanguageCodeFromRequest() {
        String result = languageService.extractLanguageCodeFromRequest();
        assertEquals("en", result);
    }

    @Test
    void testExtractLanguageCodeFromRequestWithNullParameter() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        MockitoAnnotations.openMocks(this);

        Mockito.lenient().when(request.getParameter("language")).thenReturn(mockRequest.getParameter("language"));

        String result = languageService.extractLanguageCodeFromRequest();

        assertEquals(AppConstant.DEFAULT_LANGUAGE_CODE, result);
    }

    @Test
    void testFindByCode() {
        Language language = new Language();
        Mockito.when(languageRepo.findByCode(anyString())).thenReturn(Optional.of(language));

        LanguageDTO languageDTO = new LanguageDTO();
        Mockito.when(modelMapper.map(any(), any())).thenReturn(languageDTO);

        LanguageDTO result = languageService.findByCode("en");
        assertEquals(languageDTO, result);
    }

    @Test
    void testFindByCodeWithInvalidCode() {
        Mockito.when(languageRepo.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(LanguageNotFoundException.class, () -> languageService.findByCode("invalidCode"));
    }

    @Test
    void testFindAllLanguageCodes() {
        List<String> languageCodes = new ArrayList<>();
        languageCodes.add("en");
        Mockito.when(languageRepo.findAllLanguageCodes()).thenReturn(languageCodes);

        List<String> result = languageService.findAllLanguageCodes();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByTagTranslationId() {
        Language language = new Language();
        Mockito.when(languageRepo.findByTagTranslationId(any())).thenReturn(Optional.of(language));

        LanguageDTO languageDTO = new LanguageDTO();
        Mockito.when(modelMapper.map(any(), any())).thenReturn(languageDTO);

        LanguageDTO result = languageService.findByTagTranslationId(1L);
        assertEquals(languageDTO, result);
    }
}