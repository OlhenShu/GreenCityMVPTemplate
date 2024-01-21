package greencity.service.ticket292;

import greencity.annotations.RatingCalculationEnum;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.ratingstatistics.RatingStatisticsDto;
import greencity.dto.ratingstatistics.RatingStatisticsDtoForTables;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.RatingStatisticsRepo;
import greencity.service.RatingStatisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RatingStatisticsServiceImplTest {
    @InjectMocks
    private RatingStatisticsServiceImpl ratingStatisticsService;
    @Mock
    private RatingStatisticsRepo ratingStatisticsRepo;
    @Mock
    private ModelMapper modelMapper;

    private final RatingStatistics ratingStatistics = getRatingStatistics();

    private final RatingStatisticsVO ratingStatisticsVO = getRatingStatisticsVO();

    private final RatingStatisticsDto ratingStatisticsDto = new RatingStatisticsDto(
            1L, zonedDateTime, RatingCalculationEnum.LIKE_COMMENT, 1.0f, 5.0f, getUserVO()
    );

    @Test
    void save() {
        when(ratingStatisticsRepo.save(ratingStatistics)).thenReturn(ratingStatistics);
        when(modelMapper.map(ratingStatisticsVO, RatingStatistics.class)).thenReturn(ratingStatistics);
        when(modelMapper.map(ratingStatistics, RatingStatisticsVO.class)).thenReturn(ratingStatisticsVO);
        RatingStatisticsVO expected = ratingStatisticsService.save(ratingStatisticsVO);

        verify(ratingStatisticsRepo, times(1)).save(ratingStatistics);
        assertEquals(expected, ratingStatisticsVO);
    }

    @Test
    void getRatingStatisticsForManagementByPage() {
        Page<RatingStatistics> statistics = Page.empty(PageRequest.of(0, 1));
        when(ratingStatisticsRepo.findAll(any(Pageable.class))).thenReturn(statistics);
        PageableAdvancedDto<RatingStatisticsDtoForTables> actual =
                ratingStatisticsService.getRatingStatisticsForManagementByPage(PageRequest.of(0, 1));

        PageableAdvancedDto<RatingStatisticsDtoForTables> expected =
                new PageableAdvancedDto<>(
                        List.of(), statistics.getTotalElements(), 0,
                        statistics.getTotalPages(), statistics.getNumber(), statistics.hasPrevious(),
                        statistics.hasNext(), statistics.isFirst(), statistics.isLast());

        assertEquals(expected, actual);
    }

    @Test
    void getAllRatingStatistics() {
        when(ratingStatisticsRepo.findAll()).thenReturn(Collections.singletonList(ratingStatistics));
        when(modelMapper.map(ratingStatistics, RatingStatisticsDto.class)).thenReturn(ratingStatisticsDto);
        List<RatingStatisticsDto> actual = ratingStatisticsService.getAllRatingStatistics();
        assertEquals(List.of(ratingStatisticsDto), actual);
    }

    @Test
    void getFilteredRatingStatisticsForExcel() {
        RatingStatisticsViewDto ratingStatisticsViewDto =
                new RatingStatisticsViewDto(
                        "1L", "testEvent", "2L",
                        "test@mail.com", "01.01.1970", "01.01.1970",
                        "1.0f", "5.0f"
                );

        when(ratingStatisticsRepo.findAll(any(RatingStatisticsSpecification.class))).thenReturn(List.of(ratingStatistics));
        when(modelMapper.map(ratingStatistics, RatingStatisticsDto.class)).thenReturn(ratingStatisticsDto);

        List<RatingStatisticsDto> actual =
                ratingStatisticsService.getFilteredRatingStatisticsForExcel(ratingStatisticsViewDto);

        assertEquals(List.of(ratingStatisticsDto), actual);

    }

    @Test
    void getFilteredDataForManagementByPage() {
        PageRequest request = PageRequest.of(0, 1);
        Page<RatingStatistics> statistics = Page.empty(request);
        RatingStatisticsViewDto ratingStatisticsViewDto =
                new RatingStatisticsViewDto(
                        "1L", "testEvent", "2L",
                        "test@mail.com", "01.01.1970", "01.01.1970",
                        "1.0f", "5.0f"
                );


        when(ratingStatisticsRepo.findAll(any(RatingStatisticsSpecification.class), any(Pageable.class)))
                .thenReturn(statistics);

        PageableAdvancedDto<RatingStatisticsDtoForTables> expected =
                new PageableAdvancedDto<>(
                        List.of(), statistics.getTotalElements(), 0,
                        statistics.getTotalPages(), statistics.getNumber(), statistics.hasPrevious(),
                        statistics.hasNext(), statistics.isFirst(), statistics.isLast());


        PageableAdvancedDto<RatingStatisticsDtoForTables> actual =
                ratingStatisticsService.getFilteredDataForManagementByPage(request, ratingStatisticsViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void buildSearchCriteria() {
        RatingStatisticsViewDto ratingStatisticsViewDto =
                new RatingStatisticsViewDto(
                        "1L", "name", "",
                        "", "", "",
                        "", ""
                );

        List<SearchCriteria> actual = ratingStatisticsService.buildSearchCriteria(ratingStatisticsViewDto);
        List<SearchCriteria> expected = Arrays.asList(
                SearchCriteria.builder().value("1L").key("id").type("id").build(),
                SearchCriteria.builder().value("name").key("ratingCalculationEnum").type("enum").build()
        );
        assertEquals(expected, actual);
    }
}