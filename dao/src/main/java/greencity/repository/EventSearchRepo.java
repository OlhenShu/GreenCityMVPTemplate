package greencity.repository;


import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class EventSearchRepo {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    /**
     * Initialization constructor.
     */
    public EventSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public PageableDto<SearchEventDto> find(Pageable pageable, String searchQuery, String languageCode) {
        return null;
    }
}
