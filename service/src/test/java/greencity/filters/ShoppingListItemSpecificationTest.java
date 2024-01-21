package greencity.filters;

import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemSpecificationTest {
    @Mock
    private Root<ShoppingListItem> root;
    @Mock
    private Root<ShoppingListItemTranslation> shoppingListItemTranslationRoot;
    @Mock
    private CriteriaQuery<Habit> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    private Predicate idPredicate;
    private Predicate contentPredicate;
    @Mock
    private Path<Object> objectPath;
    @Mock
    private Path<Long> idPath;
    @Mock
    private Path<String> contentPath;
    @Mock
    private Path<ShoppingListItem> pathHabitFact;
    ShoppingListItemSpecification shoppingListItemSpecification;
    private List<SearchCriteria> searchCriteriaList;

    @BeforeEach
    void init() {
        searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(SearchCriteria.builder()
                .key("id")
                .type("id")
                .value(1)
                .build());
        searchCriteriaList.add(
                SearchCriteria.builder()
                        .key(HabitFactTranslation_.CONTENT)
                        .type(HabitFactTranslation_.CONTENT)
                        .value("AnyContent")
                        .build());
        shoppingListItemSpecification = new ShoppingListItemSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        SearchCriteria searchCriteria = searchCriteriaList.get(0);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteria.getType())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, searchCriteria.getValue())).thenReturn(idPredicate);
        when(criteriaBuilder.and(expected, idPredicate)).thenReturn(expected);


        searchCriteria = searchCriteriaList.get(1);
        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(shoppingListItemTranslationRoot);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(shoppingListItemTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);
        when(criteriaBuilder.like(any(Expression.class), eq("%" + searchCriteria.getValue() + "%"))).thenReturn(contentPredicate);
        when(shoppingListItemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(pathHabitFact);
        when(pathHabitFact.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(root.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(expected);
        when(criteriaBuilder.and(expected, contentPredicate)).thenReturn(expected);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}
