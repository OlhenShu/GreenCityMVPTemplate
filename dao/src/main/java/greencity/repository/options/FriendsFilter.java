package greencity.repository.options;

import greencity.constant.RepoConstants;
import greencity.dto.filter.FriendsFilterDto;
import greencity.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FriendsFilter implements Specification<User> {
    private final transient FriendsFilterDto friendsFilterDto;

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    public FriendsFilter(FriendsFilterDto friendsFilterDto) {
        this.friendsFilterDto = friendsFilterDto;
    }

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (friendsFilterDto != null) {
            predicates.add(hasNameLike(root, criteriaBuilder, friendsFilterDto.getSearchRequest()));
        }
        if (friendsFilterDto != null && friendsFilterDto.getCity() != null) {
            predicates.add(hasCityLike(root, criteriaBuilder, friendsFilterDto.getCity()));
        }
        if (friendsFilterDto != null && friendsFilterDto.getHasMutualFriends() != null) {
            if (friendsFilterDto.getHasMutualFriends()) {
                predicates.add(hasMutualFriends(root, criteriaBuilder));
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    private Predicate hasNameLike(Root<User> root, CriteriaBuilder criteriaBuilder, String searchRequest) {
        searchRequest = replaceCriteria(searchRequest);
        return criteriaBuilder.or(
            criteriaBuilder.like(root.get(RepoConstants.NAME), searchRequest));
    }

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    private Predicate hasCityLike(Root<User> root, CriteriaBuilder criteriaBuilder, String city) {
        city = replaceCriteria(city);
        return criteriaBuilder.and(
            criteriaBuilder.equal(root.get(RepoConstants.CITY), city));
    }

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    private Predicate hasMutualFriends(Root<User> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
            criteriaBuilder.ge(root.get("mutualFriends"), 1));
    }

    /**
     * Returns a String criteria for search.
     *
     * @param criteria String for search.
     * @return String creteria not be {@literal null}.
     */
    private String replaceCriteria(String criteria) {
        criteria = Optional.ofNullable(criteria).orElseGet(() -> "");
        criteria = criteria.trim();
        criteria = criteria.replace("_", "\\_");
        criteria = criteria.replace("%", "\\%");
        criteria = criteria.replace("\\", "\\\\");
        criteria = criteria.replace("'", "\\'");
        criteria = "%" + criteria + "%";
        return criteria;
    }
}
