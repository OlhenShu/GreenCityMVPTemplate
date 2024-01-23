package greencity.filters;

import static org.mockito.Mockito.*;

import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.entity.User_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingStatisticsSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Path<Object> pathRatingStatisticsIdMock;

    @Mock
    private Path<Long> pathUserIdMock;

    @Mock
    private Root<RatingStatistics> ratingStatisticsRootMock;

    @Mock
    private Join<RatingStatistics, User> userJoinMock;

    private RatingStatisticsSpecification ratingStatisticsSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        RatingStatisticsViewDto ratingStatisticsViewDto = new RatingStatisticsViewDto("2", "UNDO_COMMENT_OR_REPLY", "1", "",
                "2021-01-12", "2021-01-13", "", "50");

        criteriaList = new ArrayList<>();
        criteriaList.add(SearchCriteria.builder().key("id").type("id").value(ratingStatisticsViewDto.getId()).build());
        criteriaList.add(SearchCriteria.builder().key("ratingCalculationEnum").type("enum")
                .value(ratingStatisticsViewDto.getEventName()).build());
        criteriaList.add(SearchCriteria.builder().key("user").type("userId").value(ratingStatisticsViewDto.getUserId())
                .build());
        criteriaList.add(SearchCriteria.builder().key("createDate").type("dateRange")
                .value(new String[]{ratingStatisticsViewDto.getStartDate(), ratingStatisticsViewDto.getEndDate()})
                .build());
        criteriaList.add(SearchCriteria.builder().key("rating").type("currentRating")
                .value(ratingStatisticsViewDto.getCurrentRating()).build());

        RatingStatistics_.user = mock(SingularAttribute.class);
        User_.id = mock(SingularAttribute.class);
        User_.email = mock(SingularAttribute.class);
        ratingStatisticsSpecification = new RatingStatisticsSpecification(criteriaList);
    }

    @Test
    void toPredicate() {
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(ratingStatisticsRootMock.get("id")).thenReturn(pathRatingStatisticsIdMock);
        when(ratingStatisticsRootMock.join(RatingStatistics_.user)).thenReturn(userJoinMock);
        when(userJoinMock.get(User_.id)).thenReturn(pathUserIdMock);
        when(criteriaBuilderMock.and(any(), any())).thenReturn(mock(Predicate.class));

        ratingStatisticsSpecification.toPredicate(ratingStatisticsRootMock, null, criteriaBuilderMock);

        verify(criteriaBuilderMock, times(5)).and(any(), any());
    }
}