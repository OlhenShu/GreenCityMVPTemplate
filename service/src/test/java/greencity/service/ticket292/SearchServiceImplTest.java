package greencity.service.ticket292;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.entity.EcoNews;
import greencity.repository.EcoNewsSearchRepo;
import greencity.service.EcoNewsService;
import greencity.service.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getSearchNewsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SearchServiceImplTest {
    @Mock
    private SearchServiceImpl searchService;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private EcoNewsSearchRepo ecoNewsRepo;
    @Mock
    private ModelMapper modelMapper;

    private final String languageCode = "en";
    private final String title = "title";


    @Test
    void search() {
        List<SearchNewsDto> searchDto = List.of(getSearchNewsDto());
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(
                searchDto, 10L, 0, 1
        );
        PageRequest request = PageRequest.of(0, 1);
        SearchResponseDto expected = SearchResponseDto
                .builder()
                .ecoNews(searchDto)
                .countOfResults(2L)
                .build();

        when(ecoNewsService.search(title, languageCode)).thenReturn(pageableDto);
        when(searchService.search(title, languageCode)).thenReturn(expected);
        when(ecoNewsRepo.find(request, title, languageCode)).thenReturn(new PageImpl<>(List.of(new EcoNews())));

        SearchResponseDto actual = searchService.search(title, languageCode);

        verify(searchService).search(title, languageCode);
        assertEquals(expected, actual);


    }

    @Test
    void searchAllNews() {
        PageRequest request = PageRequest.of(0, 1);

        List<SearchNewsDto> searchDto = Collections.singletonList(getSearchNewsDto());
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(
                searchDto, 10L, 0, 1
        );

        when(ecoNewsService.search(request, title, languageCode)).thenReturn(pageableDto);
        when(searchService.searchAllNews(request, title, languageCode)).thenReturn(pageableDto);
        when(ecoNewsRepo.find(request, title, languageCode)).thenReturn(new PageImpl<>(List.of(new EcoNews())));


        PageableDto<SearchNewsDto> actual = searchService.searchAllNews(request, title, languageCode);


        verify(searchService).searchAllNews(request, title, languageCode);
        assertEquals(pageableDto, actual);

    }
}