package greencity.filters;

import greencity.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {
    @Mock
    private Join<HabitFact, Habit> habitFactHabitJoin;
    @Mock
    private Root<HabitFact> root;
    @Mock
    private Root<HabitFactTranslation> habitFactTranslationRoot;
    @Mock
    private CriteriaQuery<Habit> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    @Mock
    private Predicate habitIdPredicate;
    private Predicate idPredicate;
    private Predicate contentPredicate;
    @Mock
    private Path<Object> objectPath;
    @Mock
    private Path<Long> idPath;
    @Mock
    private Path<String> contentPath;
    @Mock
    private Path<HabitFact> pathHabitFact;
    HabitFactSpecification habitFactSpecification;
    private List<SearchCriteria> searchCriteriaList;

    @BeforeEach
    void init() {
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(1)
                .build());
        searchCriteriaList.add(SearchCriteria.builder()
                .key("habitId")
                .type("habitId")
                .value(2)
                .build());
        searchCriteriaList.add(
                SearchCriteria.builder()
                        .key(HabitFactTranslation_.CONTENT)
                        .type(HabitFactTranslation_.CONTENT)
                        .value("AnyContent")
                        .build());
        habitFactSpecification = new HabitFactSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        SearchCriteria searchCriteria = searchCriteriaList.get(0);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteria.getType())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, searchCriteria.getValue())).thenReturn(idPredicate);
        when(criteriaBuilder.and(expected, idPredicate)).thenReturn(expected);

        searchCriteria = searchCriteriaList.get(1);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.join(HabitFact_.habit)).thenReturn(habitFactHabitJoin);
        when(habitFactHabitJoin.get(Habit_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, searchCriteria.getValue())).thenReturn(habitIdPredicate);
        when(criteriaBuilder.and(expected, habitIdPredicate)).thenReturn(expected);


        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(criteriaBuilder.like(any(Expression.class), any(String.class))).thenReturn(contentPredicate);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(pathHabitFact);
        when(pathHabitFact.get(HabitFact_.id)).thenReturn(idPath);
        when(root.get(HabitFact_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(expected);
        when(criteriaBuilder.and(expected, contentPredicate)).thenReturn(expected);

        Predicate actual = habitFactSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}